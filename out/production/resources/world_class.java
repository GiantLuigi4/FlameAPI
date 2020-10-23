public class World {
	private final %world_class% thisWorld;
	
	public World(%world_class% thisWorld) {
		this.thisWorld = thisWorld;
	}
	
	public boolean setBlockState(%block_pos_class% pos, %block_state_class% block) {
		return thisWorld.%set_block_state%(pos, block);
	}
	
	public Object getBlockState(%block_pos_class% pos) {
		return thisWorld.%get_block_state%(pos);
	}
}