package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
/**
 * Clase que representa un enemigo.
 *
 * @author Marco De Avila
 * @author Leo Garcia
 * @author Edwin Gaytan
 */
public class Enemy extends Node{
    private Node targetPosition;
    private float speed;
    private Main main;
    
    public Enemy(Node model, Vector3f spawnPosition, Node targetPosition, float speed, Main main){
        super("Enemy");
        this.attachChild(model);
        this.setLocalTranslation(spawnPosition);
        this.targetPosition = targetPosition;
        this.speed = speed;
        this.main = main;
    }
    
    public void moveToTarget(float tpf){
        Vector3f direction = targetPosition.getLocalTranslation().subtract(this.getLocalTranslation()).normalize();
        this.lookAt(targetPosition.getLocalTranslation(), Vector3f.UNIT_Y);
        this.move(direction.mult(tpf * speed));
        
        float distance = this.getLocalTranslation().distance(targetPosition.getLocalTranslation());
        if(distance <= 1.0f){
            main.anemoneHealth -= 10;
            main.updateHealth();
            this.removeFromParent();
        }
    }
}
