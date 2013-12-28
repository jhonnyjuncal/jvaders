package com.jhonny.jvaders;

import org.andengine.util.adt.pool.GenericPool;


public class EnemyPool extends GenericPool<Enemy> {
	
	public static EnemyPool instance;
	
	
	private EnemyPool(){
		super();
	}
	
	public static EnemyPool sharedEnemyPool(){
		if(instance == null)
			instance = new EnemyPool();
		return instance;
	}
	
	@Override
	protected Enemy onAllocatePoolItem() {
		return new Enemy();
	}
	
	/** Called when a projectile is sent to the pool */
	protected void onHandleRecycleItem(final Enemy e) {
	    e.sprite.setVisible(false);
	    e.sprite.detachSelf();
	    e.clean();
	}
}
