public void equipStack(Object slot, Object stack) {
}
public final void %equip_stack%(%equipment_slot_class% slot, %item_stack_class% stack) {
	equipStack((Object)slot,(Object)stack);
}
public void write(Object nbt) {
	super.%writeNBT%((%nbt_class%)nbt);
}
public void read(Object nbt) {
	super.%readNBT%((%nbt_class%)nbt);
}
public Entity(%entity_type_class% type, %world_class% world) {
	super(type,world);
}
public void tick() {
	super.%tick%();
}
public final void %tick%() {
	tick();
}
public Object getEquippedStack(Object slot) {
	return null;
}
public %item_stack_class% %get_equipped_stack%(%equipment_slot_class% slot) {
	return (%item_stack_class%)getEquippedStack(slot);
}
public final double getX() {
	return %getX%;
}
public final double getY() {
	return %getY%;
}
public final double getZ() {
	return %getZ%;
}
public void move(double x, double y, double z) {
	super.%move%(x,y,z);
}
public void %move%(double x, double y, double z) {
	move(x,y,z);
}
public final void %writeNBT%(%nbt_class% nbt) {
	write(nbt);
}
public final void %readNBT%(%nbt_class% nbt) {
	read(nbt);
}
public Iterable<%item_stack_class%> %get_armor_items%() {
	//TODO
	return new Iterable<%item_stack_class%>();
}
public %arm_class% %get_main_arm%() {
	//TODO
	return %arm_class%.values()[0];
}