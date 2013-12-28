package com.jhonny.jvaders;

import java.util.Iterator;
import java.util.LinkedList;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityFactory;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;


public class GameScene extends Scene implements IOnSceneTouchListener {
	
	public Ship ship;
	Camera mCamera;
	float accelerometerSpeedX;
	SensorManager sensorManager;
	public LinkedList<Bullet> bulletList;
	int bulletCount;
	int missCount;
	
	
	public GameScene() {
	    setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
	    mCamera = BaseActivity.getSharedInstance().mCamera;
	    ship = Ship.getSharedInstance();
	    bulletList = new LinkedList<Bullet>();
	    attachChild(ship.sprite);
	    
	    // attaching an EnemyLayer entity with 12 enemies on it
	    attachChild(new EnemyLayer(12));
	    
	    
	    setOnSceneTouchListener(this);
	    BaseActivity.getSharedInstance().setCurrentScene(this);
	    sensorManager = (SensorManager) BaseActivity.getSharedInstance().getSystemService(BaseGameActivity.SENSOR_SERVICE);
	    SensorListener.getSharedInstance();
	    sensorManager.registerListener(SensorListener.getSharedInstance(),
	    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
	    
	    resetValues();
	}
	
	public void moveShip() {
	    ship.moveShip(accelerometerSpeedX);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		synchronized(this){
			if(!CoolDown.getSharedInstance().checkValidity())
			    return false;
			ship.shoot();
		}
		return true;
	}
	
	public void cleaner() {
		synchronized (this) {
			// if all Enemies are killed
			if (EnemyLayer.isEmpty()) {
			    setChildScene(new ResultScene(mCamera));
			    clearUpdateHandlers();
			}
			Iterator<Enemy> eIt = EnemyLayer.getIterator();
            while (eIt.hasNext()) {
                Enemy e = eIt.next();
                Iterator<Bullet> it = bulletList.iterator();
                while (it.hasNext()) {
                    Bullet b = it.next();
                    if(b.sprite.getY() <= -b.sprite.getHeight()){
                    	BulletPool.sharedBulletPool().recyclePoolItem(b);
                        it.remove();
                        continue;
                    }
 
                    if (b.sprite.collidesWith(e.sprite)) {
                        if (!e.gotHit()) {
                        	createExplosion(e.sprite.getX(), e.sprite.getY(), e.sprite.getParent(), BaseActivity.getSharedInstance());
                            EnemyPool.sharedEnemyPool().recyclePoolItem(e);
                            eIt.remove();
                        }
	                    BulletPool.sharedBulletPool().recyclePoolItem(b);
	                    it.remove();
	                    break;
	                }else
	                	missCount++;
	            }
	        }
		}
	}
	
	// method to reset values and restart the game
	public void resetValues() {
	    missCount = 0;
	    bulletCount = 0;
	    ship.restart();
	    EnemyLayer.purgeAndRestart();
	    clearChildScene();
	    registerUpdateHandler(new GameLoopUpdateHandler());
	}
	
	public void detach() {
	    clearUpdateHandlers();
	    for (Bullet b : bulletList) {
	        BulletPool.sharedBulletPool().recyclePoolItem(b);
	    }
	    bulletList.clear();
	    detachChildren();
	    Ship.instance = null;
	    EnemyPool.instance = null;
	    BulletPool.instance = null;
	}
	
	private void createExplosion(final float posX, final float posY, final IEntity target, final SimpleBaseGameActivity activity) {
		int mNumPart = 15;
		int mTimePart = 2;
		 
		PointParticleEmitter particleEmitter = new PointParticleEmitter(posX,posY);
		IEntityFactory recFact = new IEntityFactory() {
		    @Override
		    public Rectangle create(float pX, float pY) {
		        Rectangle rect = new Rectangle(posX, posY, 10, 10, activity.getVertexBufferObjectManager());
		        rect.setColor(Color.GREEN);
		        return rect;
		    }
		};
		final ParticleSystem particleSystem = new ParticleSystem( recFact, particleEmitter, 500, 500, mNumPart);
		particleSystem.addParticleInitializer(new VelocityParticleInitializer(-50, 50, -50, 50));
		
		particleSystem.addParticleModifier(new AlphaParticleModifier(0,0.6f * mTimePart, 1, 0));
		particleSystem.addParticleModifier(new RotationParticleModifier(0, mTimePart, 0, 360));
		
		target.attachChild(particleSystem);
		target.registerUpdateHandler(new TimerHandler(mTimePart, new ITimerCallback() {
		    @Override
		    public void onTimePassed(final TimerHandler pTimerHandler) {
		        particleSystem.detachSelf();
		        target.sortChildren();
		        target.unregisterUpdateHandler(pTimerHandler);
		    }
		}));
	}
}
