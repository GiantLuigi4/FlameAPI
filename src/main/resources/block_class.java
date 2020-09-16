public class Block extends %block_class% {
	public final com.tfc.API.flamemc.blocks.Block thisBlock;
	
	public Block(com.tfc.API.flamemc.blocks.Block thisBlock, %properties_class% properties) {
		super(properties);
		this.thisBlock = thisBlock;
	}
	
	public void onRemoved(Object[] args) {
		%callInfoGen_onRemoved%;
		this.thisBlock.onRemoved(info);
	}
}