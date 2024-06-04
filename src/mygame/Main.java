package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.texture.Texture;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;




/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    private static Main app;
// Camara
    private FilterPostProcessor fpp;
    
    // Game
    private Spatial clownfish;
    private Node centerObject;    
    private float speed = 30f;
    private float orbitRadius = 3.0f;
    private AudioNode audio;
    
    // Inputs
    private final static Trigger TRIGGER_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_CLICK = "Click";
    
    

    public static void main(String[] args) {
        app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        CamSettings();
        LoadAnemone();
        loadAndPlayMusic();
        
        inputManager.addMapping(MAPPING_CLICK, TRIGGER_CLICK);
        inputManager.addListener(actionListener, new String[]{MAPPING_CLICK});
    }
    /*
        Metodo que cambia los ajustes de la camara.
    */
    private void CamSettings(){
        fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Scene);
        bloom.setBloomIntensity(1);
        //fpp.addFilter(bloom);
      
        viewPort.addProcessor(fpp);  
        
        
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0f, -90f, -65)); 
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        cam.setLocation(new Vector3f(0f, 0f, 15f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Z);
    }
    /*
        Metood que carga la anemona y el pez payaso.
    */
    private void LoadAnemone(){
        assetManager.registerLocator("assets/", FileLocator.class);
        clownfish = assetManager.loadModel("Models/clownfish.j3o");
        clownfish.scale(0.3f);
        
        centerObject = (Node) assetManager.loadModel("Models/anemone.j3o");
        centerObject.scale(0.5f);
        centerObject.move(0, -5, 0);
        rootNode.attachChild(centerObject);
        centerObject.attachChild(clownfish);
        clownfish.setLocalTranslation(4, 4, 0);
        
        
        Node reef = (Node) assetManager.loadModel("Models/reef/reef.j3o");
        reef.scale(5f);
        reef.move(0, -6, 0);
        rootNode.attachChild(reef);
    }
    /*
        Metodo que regresa particulas de burbujas.
    */
    private void Bubbles(Vector3f pos){
        Texture texture = assetManager.loadTexture("Textures/bubble_1.png");
        Geometry sphere = new Geometry("Sphere", new Sphere(32, 32, 10));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White); // Puedes cambiar el color según tus necesidades
        sphere.setMaterial(mat);
        System.out.println(pos);
        sphere.setLocalTranslation(new Vector3f(0, 0, -10));
        rootNode.attachChild(sphere);
    }
    
    /*
    Metodo para cargar la musica
    */
    
     public void loadAndPlayMusic() {
        // Cargar el archivo de audio OGG
        audio = new AudioNode(assetManager, "Sounds/AquaSong.wav", DataType.Stream);
        audio.setLooping(true); // Para que la música se repita
        audio.setPositional(false); // La música de fondo no es posicional
        audio.setVolume(8); // Ajusta el volumen

        // Adjuntar el nodo de audio al nodo raíz
        rootNode.attachChild(audio);

        // Reproducir el audio
        audio.play();
    }
    
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
            public void onAction(String name, boolean isPressed, float tpf){
                if(name.equals(MAPPING_CLICK) && !isPressed){
                    CollisionResults results = new CollisionResults();
                    // CLICK
                    Vector2f click2d = inputManager.getCursorPosition();
                    Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 0f);
                    Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 1f).subtractLocal(click3d);
                    Ray ray = new Ray(click3d, dir);
                    rootNode.collideWith(ray, results);
                    //Bubbles(click3d);
                }
            }
    };
    
    @Override
    public void simpleUpdate(float tpf) {
        RotateClownFish(tpf);
    }
    /*
        Metodo que rota al pez payaso sobre la anemona.
    */
    private void RotateClownFish(float tpf){
        float angleChange = -FastMath.DEG_TO_RAD * speed * tpf;
        Quaternion rotation = new Quaternion().fromAngleAxis(angleChange, Vector3f.UNIT_Y);

        // Calcular la nueva posición del objeto en su órbita
        Vector3f rotatedPosition = rotation.mult(clownfish.getLocalTranslation().subtract(centerObject.getLocalTranslation()));

        // Aplicar la rotación al objeto que se está rotando
        clownfish.setLocalTranslation(centerObject.getLocalTranslation().add(rotatedPosition));

        // Rotar el objeto alrededor del centro
        clownfish.rotate(rotation);
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
