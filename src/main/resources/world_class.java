public class World extends %world_class% {
	public boolean setBlockState(com.tfc.API.flamemc.BlockPos pos, %block_class% block) {
		return super.%set_block_state%(pos.unWrap(), block);
	}
	
	public Object getBlockState(com.tfc.API.flamemc.BlockPos pos) {
		return super.%get_block_state%(pos.unWrap());
	}
}