package com.ceteva.oleBridge;

import XOS.Message;
import XOS.Value;

import com.ceteva.client.Client;
import com.ceteva.client.EventHandler;

public class OleBridgeClient extends Client {
	
	public OleBridgeClient() {
		super("com.ceteva.oleBridge"); 
	}
	
	public Value processCall(Message message) {
		if(message.hasName("getProperty") && message.arity == 2) {
		  String id = message.args[0].strValue();
		  String property = message.args[1].strValue();
		  try {
		    return Component.get(id,property);
		  }
		  catch(DispatchException de) {
			de.printStackTrace(); 
		    return null;	  
		  }
		}
		if(message.hasName("newTopLevel") && message.arity == 2) {
		  String id = message.args[0].strValue();
		  String target = message.args[1].strValue();
		  Component.newTopLevel(id,target);
		  return new Value(true);
		}
		else if(message.hasName("call") && message.arity == 3) {
		  String target = message.args[0].strValue();
		  String call = message.args[1].strValue();
		  Value[] args = message.args[2].values;
		  try {
			Component.call(target,call,args);
			return new Value(true);
		  }
		  catch(DispatchException de) {
			de.printStackTrace();
			return new Value(false);
		  }
		}
		else if(message.hasName("callAndStore") && message.arity == 4) {
		  String target = message.args[0].strValue();
		  String store = message.args[1].strValue();
		  String call = message.args[2].strValue();
		  Value[] args = message.args[3].values;
		  try {
		    Component.callAndStore(target,store,call,args);
			return new Value(true);
		  }
		  catch(DispatchException de) {
			de.printStackTrace();
			return new Value(false);
		  }
		}
		else if(message.hasName("getObject") && message.arity == 3) {
		  String target = message.args[0].strValue();
		  String property = message.args[1].strValue();
		  String store = message.args[2].strValue();
		  try {
			Component.getObject(target,property,store);
			return new Value(true);
		  }
		  catch(DispatchException de) {
			de.printStackTrace();
			return new Value(false);
		  }
		}
		else if(message.hasName("setProperty") && message.arity == 3) {
		  String target = message.args[0].strValue();
		  String property = message.args[1].strValue();
		  Value value = message.args[2];
		  try {
			Component.set(target,property,value);
			return new Value(true);
		  }
		  catch(DispatchException de) {
			de.printStackTrace();
			return new Value(false);
		  }
		}
		else if(message.hasName("setObject") && message.arity == 3) {
		  String target = message.args[0].strValue();
		  String property = message.args[1].strValue();
		  String object = message.args[3].strValue();
		  try {
			Component.setToDispatch(target,property,object);
			return new Value(true);
		  }
		  catch(DispatchException de) {
			de.printStackTrace();
			return new Value(false);
		  }
		}		
		return null;
	}

	public boolean processMessage(Message message) {
		return false;
	}

	public void setEventHandler(EventHandler handler) {
	    // Events are not raised from the OLE client
		
	}

}