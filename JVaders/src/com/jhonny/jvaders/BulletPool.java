package com.jhonny.jvaders;

import org.andengine.util.adt.pool.GenericPool;


public class BulletPool extends GenericPool<Bullet> {
	
	public static BulletPool instance;
	
	
	private BulletPool() {
		super();
	}
	
	public static BulletPool sharedBulletPool() {
		if(instance == null)
        	instance = new BulletPool();
		return instance;
	}
	
	@Override
	protected Bullet onAllocatePoolItem() {
		return new Bullet();
	}
	
	protected void onHandleRecycleItem(final Bullet b) {
		b.sprite.clearEntityModifiers();
		b.sprite.clearUpdateHandlers();
		b.sprite.setVisible(false);
		b.sprite.detachSelf();
	}
}
