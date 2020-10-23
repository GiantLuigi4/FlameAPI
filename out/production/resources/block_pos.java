public class BlockPos extends %block_pos_class% {
	public final int x;
	public final int y;
	public final int z;
	
	public BlockPos(int x, int y, int z) {
		super(x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockPos offset(int x, int y, int z) {
		return new BlockPos(this.x + x, this.y + y, this.z + z);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
}