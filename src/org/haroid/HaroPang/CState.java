package org.haroid.HaroPang;

 enum  STATE_ID {PREFIX_S, CMD_S, RSVD_S,SUBCMD_S,LENGTH_S,PAYLOAD_S, POSTFIX_S, COMPLETE_S, FAIL_S,
        SYNC_LENGTH_S, SYNC_PAYLOAD_S, SYNC_POSTFIX_S, SYNC_COMPLETE_S, SYNC_FAIL_S };

 public class CState {

    STATE_ID m_stateID;

    STATE_ID GetState() {
		return m_stateID;
	}
		  
	STATE_ID Do(byte pk) {
    	
    	return m_stateID;
    }

}

