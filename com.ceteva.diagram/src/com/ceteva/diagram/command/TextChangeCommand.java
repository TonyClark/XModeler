package com.ceteva.diagram.command;

import org.eclipse.gef.commands.Command;

import com.ceteva.diagram.model.Text;

public class TextChangeCommand extends Command {

  private String newName;
  private Text text;

  public TextChangeCommand(Text text, String string) {
	this.text = text;
	if (string != null)
		newName = string;
	else
		newName = "";  //$NON-NLS-1$
  }

  public void execute() {
	text.changeText(newName);
  }

  public void undo() {
  }
}