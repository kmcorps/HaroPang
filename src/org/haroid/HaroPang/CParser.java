package org.haroid.HaroPang;

import android.util.Log;

public class CParser {
	  
	  public  CParser(){
		  State_Prefix(); 
		m_parsePacket = new byte[50];
		  m_cmdPkt = new CCommand();
		  m_syncPkt = new CSyncCommand();
	  }
	  public  CParser(CState pNewState){
		  m_pState = pNewState;
		m_parsePacket = new byte[50];
	  }
	    
	public byte[] m_parsePacket;
	CCommand m_cmdPkt;
	    CSyncCommand  m_syncPkt;
	byte m_nLength; // payload length
	byte m_nPayload; // couting variable
	byte m_PostfixPredict;
	byte m_fSync;

	    
	    public CState m_pState;
		
	    void ChangeState(CState pNewState){
			   m_pState = pNewState;
		}

	    
	STATE_ID Update(byte pk) {
		  
		    STATE_ID ret = STATE_ID.FAIL_S;

		    m_pState.Do(pk);

		   
		    if(m_pState.GetState() == STATE_ID.COMPLETE_S) {
		        //wrbPutbyte(0x00);
		        ret = STATE_ID.COMPLETE_S;
		        
		        m_pState.Do(pk);
		        //SendMessage(&m_cmdPkt);
		        
		        State_Prefix();
		        
		        
		    } else if(m_pState.GetState() == STATE_ID.FAIL_S) {
		        ret = STATE_ID.FAIL_S;
		      
		        m_pState.Do(pk);    
		        
		        State_Prefix();
		      
		    } else
		    {
		        ret = m_pState.GetState();
		    }
		    
		    return ret;

	  }

	  
	  
	  public void State_Prefix(){
		  ChangeState(new CPrefixState(this));
	  }
	  public void State_Cmd(){
		  ChangeState(new CCmdState(this));
	  }
	  public void State_Reserved(){
		  ChangeState(new CReservedState(this));
	  }
	  public void State_SubCmd(){
		  ChangeState(new CSubCmdState(this));
	  }
	  public void State_Length(){
		  ChangeState(new CLengthState(this));
	  }
	  public void State_Payload(){
		  ChangeState(new CPayloadState(this));
	  }
	  public void State_Postfix(){
		  ChangeState(new CPostfixState(this));
	  } 
	  public void State_Complete(){
		  ChangeState(new CCompleteState(this));
	  }
	  public void State_Fail(){
		  ChangeState(new CFailState(this));
	  }

	  public void State_SyncLength(){
		  ChangeState(new CSyncLengthState(this));
	  }
	  public void State_SyncPayload(){
		  ChangeState(new CSyncPayloadState(this));
	  }
	  public void State_SyncPostfix(){
		  ChangeState(new CSyncPostfixState(this));
	  } 
	  public void State_SyncComplete(){
		  ChangeState(new CSyncCompleteState(this));
	  }
	  public void State_SyncFail(){
		  ChangeState(new CSyncFailState(this));
	  }

	   
	  
 

		public class CCommand {
		byte CMD;
		byte Reserved; // 0xFF
		byte SubCMD; //
		byte length; // payload length
		byte[] payload; // payload : max 256 . but you have to care a RAM 2K
						// size.
			  
			  public CCommand() {
			payload = new byte[8];
			  }

		public void toObject(byte[] m_parsePacket) {
			CMD = m_parsePacket[0];
			Reserved = m_parsePacket[1];
			SubCMD = m_parsePacket[2];
			length = m_parsePacket[3];
				  
				  for(int i=0;i<length;i++) {
				payload[i] = m_parsePacket[4 + i];
				  }
					  
			  }
			  
		}

	   public class CSyncCommand {
		byte Status; // 1:ok, 0: fail
		byte length; // data length
		byte[] data; // data : max 256 . but you have to care a RAM 2K size.
		   public CSyncCommand() {
			data = new byte[4];
		   }
  		  
		public void toObject(byte[] m_parsePacket) {
			Status = m_parsePacket[0];
			length = m_parsePacket[1];
		  
			  for(int i=0;i<length;i++) {
				data[i] = m_parsePacket[2 + i];
			  }
				  
		   }

	   }
	   
	   
	   
	   public class CPrefixState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CPrefixState(CParser parse) {
			     m_stateID = STATE_ID.PREFIX_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
		      	   
			Log.i("Parser", "Prefix State");
			if (pk == (byte) 0xff)
		    	    {
		    	       m_Parse.State_Cmd();
		    	       
		    	       return STATE_ID.CMD_S;
		    	    }
		    	    else
		    	    {
		    	      
		    	      m_Parse.State_Fail();
		    	      return STATE_ID.FAIL_S;
		    	    }	 
		     
		     }
	   }
	   public class CCmdState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CCmdState(CParser parse) {
			     m_stateID = STATE_ID.CMD_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			   STATE_ID ret;
			Log.i("Parser", "Cmd State");
			if (((pk & 0xF0) >> 4) == (byte) 0x00) { // CMD
			        m_Parse.m_parsePacket[0] = pk;
				m_Parse.m_fSync = (byte) (pk & 0x01);
			        
			        m_Parse.State_Reserved();
			        
			        ret = STATE_ID.RSVD_S;
			      
			    } else {  // SYNC_CMD
				m_Parse.m_parsePacket[0] = (byte) (pk & 0x01);
			      
			        m_Parse.State_SyncLength();
			        ret = STATE_ID.SYNC_LENGTH_S;
			    }
			    return ret;
		   
		   }
	   }
	   public class CReservedState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CReservedState(CParser parse) {
			     m_stateID = STATE_ID.RSVD_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			Log.i("Parser", "Reserved State");
			if (pk == (byte) 0xFF)
			   {
			     m_Parse.m_parsePacket[1] = pk;
			     m_Parse.State_SubCmd();
			     return STATE_ID.SUBCMD_S;
			     
			   } else {
			     m_Parse.State_Fail();
			     return STATE_ID.FAIL_S;
			   }			   
		   }
	   }
	   
	   public class CSubCmdState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CSubCmdState(CParser parse) {
			     m_stateID = STATE_ID.SUBCMD_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			Log.i("Parser", "SubCmd State");

			   m_Parse.m_parsePacket[2] = pk;
			   
			   m_Parse.State_Length();
			   return STATE_ID.LENGTH_S;
		   
		   }
		   
	   }
	   public class CLengthState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CLengthState(CParser parse) {
			     m_stateID = STATE_ID.LENGTH_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			Log.i("Parser", "Length State");

			   m_Parse.m_parsePacket[3] = pk;
			   m_Parse.m_nLength = pk;
			m_Parse.m_PostfixPredict = (byte) (m_Parse.m_nLength ^ 0xFF);
			   
			if (pk != (byte) 0x00)
			   {
			     m_Parse.State_Payload();
			     return STATE_ID.PAYLOAD_S;
			   }
			   else
			   {
			       m_Parse.State_Postfix();
			      return STATE_ID.POSTFIX_S;
			   }

			   
		   }
	   
	   }
	   public class CPayloadState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CPayloadState(CParser parse) {
			     m_stateID = STATE_ID.PAYLOAD_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			Log.i("Parser", "Payload State");

			   m_Parse.m_parsePacket[4+m_Parse.m_nPayload]=pk;
			   ++m_Parse.m_nPayload;
			   if(m_Parse.m_nPayload < m_Parse.m_nLength)
			   {
			     m_Parse.State_Payload();
			     return STATE_ID.PAYLOAD_S;
			   }
			   else
			   {
			     m_Parse.State_Postfix();
			     return STATE_ID.POSTFIX_S;
			   }    
			   
		   }
		   
	   }
	   public class CPostfixState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CPostfixState(CParser parse) {
			     m_stateID = STATE_ID.POSTFIX_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			Log.i("Parser", "Postfix State");

			   if(pk == m_Parse.m_PostfixPredict)
			   {
			     m_Parse.State_Complete();
			     return STATE_ID.COMPLETE_S;
			   } 
			   else
			   {
			     m_Parse.State_Fail();
			     return STATE_ID.FAIL_S;
			   }
			   
		   }
		   
		   
	   }
	   
	   public class CCompleteState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CCompleteState(CParser parse) {
			     m_stateID = STATE_ID.COMPLETE_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			Log.i("Parser", "Complete State");
			   // very good!!! 
			   m_Parse.m_cmdPkt.toObject(m_Parse.m_parsePacket);  
			        
			// m_Parse.m_nLength = 0; // payload length
			// m_Parse.m_nPayload = 0; // couting variable
			// m_Parse.m_PostfixPredict = 0;
			   
			   // the Complete state is null state. because nothing Do!!!
			 //  m_Parse->State_Prefix();

			   return STATE_ID.COMPLETE_S;  
			   
		   }
		   
	   }
	   public class CFailState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CFailState(CParser parse) {
			     m_stateID = STATE_ID.FAIL_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			   // initialize    
			Log.i("Parser", "Fail State");

			   m_Parse.m_nLength = 0;         // payload length
			   m_Parse.m_nPayload = 0;        // couting variable
			   m_Parse.m_PostfixPredict = 0;  

			 //  m_Parse->State_Prefix(); 

			   return STATE_ID.FAIL_S;   

		   }
		   
	   }
	   
		   
	   public class CSyncLengthState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CSyncLengthState(CParser parse) {
			     m_stateID = STATE_ID.SYNC_LENGTH_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			   m_Parse.m_parsePacket[1] = pk;
			   m_Parse.m_nLength = pk;
			m_Parse.m_PostfixPredict = (byte) (m_Parse.m_nLength ^ 0xFF);
			   
			if (pk != (byte) 0x00)
			   {
			     m_Parse.State_SyncPayload();
			     return STATE_ID.SYNC_PAYLOAD_S;
			   }
			   else
			   {
			       m_Parse.State_SyncPostfix();
			      return STATE_ID.SYNC_POSTFIX_S;
			   }

			   
		   }
		   

	   }
	   
	   public class CSyncPayloadState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CSyncPayloadState(CParser parse) {
			     m_stateID = STATE_ID.SYNC_PAYLOAD_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			   m_Parse.m_parsePacket[2+m_Parse.m_nPayload]=pk;
			   ++m_Parse.m_nPayload;
			   if(m_Parse.m_nPayload < m_Parse.m_nLength)
			   {
			     m_Parse.State_SyncPayload();
			     return STATE_ID.SYNC_PAYLOAD_S;
			   }
			   else
			   {
			     m_Parse.State_SyncPostfix();
			     return STATE_ID.SYNC_POSTFIX_S;
			   }    

		   }
		   
	  
	   }
	   
	   public class CSyncPostfixState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CSyncPostfixState(CParser parse) {
			     m_stateID = STATE_ID.SYNC_POSTFIX_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			   if(pk == m_Parse.m_PostfixPredict)
			   {
			     m_Parse.State_SyncComplete();
			     return STATE_ID.SYNC_COMPLETE_S;
			   } 
			   else
			   {
			     m_Parse.State_SyncFail();
			     return STATE_ID.SYNC_FAIL_S;
			   }
		   }
		   
	   
	   }
	   
	   public class CSyncCompleteState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CSyncCompleteState(CParser parse) {
			     m_stateID = STATE_ID.SYNC_COMPLETE_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			   // very good!!! 
			   m_Parse.m_syncPkt.toObject(m_Parse.m_parsePacket);  
			        
			   m_Parse.m_nLength = 0;         // payload length
			   m_Parse.m_nPayload = 0;        // couting variable
			   m_Parse.m_PostfixPredict = 0;
			   
			   // the Complete state is null state. because nothing Do!!!
			 //  m_Parse->State_Prefix();

			   return STATE_ID.SYNC_COMPLETE_S;  

		   }
		   
	   
	   }
	   public class CSyncFailState extends CState {
		   private CParser m_Parse;

		     //CPrefixState():CState(PREFIX_S) {};
		   public  CSyncFailState(CParser parse) {
			     m_stateID = STATE_ID.SYNC_FAIL_S;
		    	 m_Parse = parse;
		     }
		    
		public STATE_ID Do(byte pk) {
			   // initialize    
			   m_Parse.m_nLength = 0;         // payload length
			   m_Parse.m_nPayload = 0;        // couting variable
			   m_Parse.m_PostfixPredict = 0;  

			 //  m_Parse->State_Prefix(); 

			   return STATE_ID.SYNC_FAIL_S;   

		   }
		   
	   
	   }


}
