package com.ceteva.diagram.clipboard;

import java.io.IOException;
import java.io.InputStream;

import com.ceteva.diagram.stubs.BITMAPINFOHEADER;
import com.ceteva.diagram.stubs.OS;

class UncompressDibFilterInputStream extends InputStream {

      private final InputStream in;
      private byte[] buffer;
      private int index = 0;
      private final boolean isCompressed;

      //The following are only used when isCompressed==true
      /** bits per pixel */
      private short bitCount = -1;
      private int redMask = -1;
      private int greenMask = -1;
      private int blueMask = -1;

      public UncompressDibFilterInputStream(InputStream bmpFileStream)
                      throws IOException {
              this.in = bmpFileStream;
              this.buffer = new byte[BITMAPINFOHEADER.sizeof];

              //read in the BITMAPINFOHEADER from the stream
              this.in.read(this.buffer, 0, this.buffer.length);
              BITMAPINFOHEADER origInfoHeader = new BITMAPINFOHEADER();
              ConversionUtil.fromBytes(origInfoHeader, this.buffer, 0);

              this.isCompressed = origInfoHeader.biCompression == OS.BI_BITFIELDS;

              if (this.isCompressed) {
                      this.bitCount = origInfoHeader.biBitCount;

                      origInfoHeader.biCompression = OS.BI_RGB;
                      origInfoHeader.biSizeImage = 0;

                      ConversionUtil.toBytes(origInfoHeader, this.buffer, 0);

                      //read the next 12 bytes and just ignore them
                      final byte[] redMaskBytes = new byte[4];
                      final byte[] greenMaskBytes = new byte[4];
                      final byte[] blueMaskBytes = new byte[4];

                      this.in.read(redMaskBytes);
                      this.in.read(greenMaskBytes);
                      this.in.read(blueMaskBytes);

                      this.redMask = ConversionUtil.bytesToInt(redMaskBytes, 0);
                      this.greenMask = ConversionUtil.bytesToInt(greenMaskBytes, 0);
                      this.blueMask = ConversionUtil.bytesToInt(blueMaskBytes, 0);
              }
      }

      public int read() throws IOException {
              //first try and read from the buffer
              if (this.index < this.buffer.length) {
                      final byte b = this.buffer[this.index++];
                      return 0xff & b;
              }

              //do bitmask conversions and throw them in the buffer.
              if (this.isCompressed) {
                      switch (this.bitCount) {
                              case 16 :
                                      //each pixel is a WORD (short) (2 bytes)
                                      final byte[] pixelBytes = new byte[2];
                                      final short pixel;
                                      final int red;
                                      final int green;
                                      final int blue;
                                      //used in calculating the new pixelBytes
                                      final short a;
                                      final short b;
                                      final short c;
                                      final short newPixel;

                                      this.in.read(pixelBytes);
                                      pixel = ConversionUtil.bytesToShort(pixelBytes, 0);
                                      red = deMask(pixel, this.redMask);
                                      green = deMask(pixel, this.greenMask);
                                      blue = deMask(pixel, this.blueMask);

                                      //since green was 6 bits before, shift it down to 5
                                      a = (short) ((0x1f) & blue);
                                      b = (short) ((0x3e0) & ((green >> 1) << 5));
                                      c = (short) ((0x7c00) & (red << 10));
                                      newPixel = (short) (a + b + c);

                                      this.buffer = new byte[2];
                                      ConversionUtil.shortToBytes(newPixel, this.buffer, 0);
                                      this.index = 1;
                                      return 0xff & this.buffer[0];

                              default :
                      }
              }

              return this.in.read();
      }

      /**
       * not too elegant way of reading bitmasked color info.
       * val.. : 10110100 10011011
       * mask. : 00000111 11100000
       * return: 00100100
       */
      private static int deMask(int val, int mask) {
              int a = val & mask;
              while (mask % 2 == 0) {
                      a = a >> 1;
                      mask = mask >> 1;
              }
              return a;
      }
}