/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

/**
 *
 * @author kille
 */
public class ParticlesExample extends SimpleApplication{

    private Node emitterNode;
    
    public static void main(String[] args) {
        ParticlesExample app = new ParticlesExample();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        emitterNode = new Node("Emitter Node");
        rootNode.attachChild(emitterNode);
        createParticleEmitter();
        
    }
    
    private void createParticleEmitter(){
        Texture texture = assetManager.loadTexture("Textures/bubble_1.png");
        ParticleEmitter emitter = new ParticleEmitter("Emitter", Type.Triangle, 300);
        emitter.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md"));
        emitter.getMaterial().setTexture("Texture", texture);
        emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        emitter.setStartSize(0.3f);
        emitter.setEndSize(0.05f);
        emitter.setLowLife(1f);
        emitter.setHighLife(3f);
        emitterNode.attachChild(emitter);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
    }
}
