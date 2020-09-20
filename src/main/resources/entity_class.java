public class Entity%name% extends %entity_class% {
	%fields%
	
	public void %tick_method% {
		com.tfc.API.flamemc.abstraction.CallInfo info = new com.tfc.API.flamemc.abstraction.CallInfo(
				argsUpdated,
				%argsUpdated%
		);
		%tick_code%
	}
}