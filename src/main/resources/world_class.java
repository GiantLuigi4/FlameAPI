public class World extends %world_class% {
	public void setBlockState(com.tfc.API.flamemc.BlockPos pos, %block_class% block) {
		super.%set_block_state%(pos.unWrap(), block);
	}
	
	public void getBlockState(com.tfc.API.flamemc.BlockPos pos) {
		super.%get_block_state%(pos.unWrap());
	}
}