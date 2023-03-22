import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ActiveClientsTest {

	@Test
	void checkSingleInstance() {
		
		ActiveClients instance1 = ActiveClients.getInstance();
		ActiveClients instance2 = ActiveClients.getInstance();
		
		assertEquals(instance1, instance2);		
	}
	
}
