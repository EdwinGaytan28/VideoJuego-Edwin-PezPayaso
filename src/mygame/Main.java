package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
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
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Esta clase carga la logica del juego y los estados de este
 * como el comienzo, update, y el gameover.
 * 
 * @author Marco De Avila
 * @author Leo Garcia
 * @author Edwin Gaytan
 */
public class Main extends SimpleApplication {
    private static Main app;
    
    // Camara
    private FilterPostProcessor fpp;
    
    // Game
    private int score = 0;
    public int anemoneHealth = 250;
    private AudioNode audio;
    private BitmapText scoreText;
    private BitmapText healthText;
    private BitmapText gameOverText;
    
    private final static Trigger TRIGGER_RESTART = new KeyTrigger(KeyInput.KEY_R);
    private final static String MAPPING_RESTART = "Restart";
    
    private Spatial clownfish;
    private Node centerObject;    
    private float speed = 30f;
    private float orbitRadius = 3.0f;
    
    // Inputs
    private final static Trigger TRIGGER_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_CLICK = "Click";
    
    // Enemis
    private Spatial lionFish;
    private float enemiSpeed = 15f;
    private float spawntime = 1.5f;
    
    
    public static void main(String[] args) {
        app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Edwin: Clownfish");
        settings.setSettingsDialogImage("Textures/foto.png");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        CamSettings();
        LoadAnemone();
        LoadEnemis();
        loadAndPlayMusic();
        initScoreText();
        initHealthText();
        
        inputManager.addMapping(MAPPING_CLICK, TRIGGER_CLICK);
        inputManager.addListener(actionListener, new String[]{MAPPING_CLICK});
        
        inputManager.addMapping(MAPPING_RESTART, TRIGGER_RESTART);
        inputManager.addListener(actionListener, MAPPING_RESTART);
    }
    /*
        Metodo que cambia los ajustes de la camara.
    */
    private void CamSettings(){
        fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Scene);
        bloom.setBloomIntensity(2);
        fpp.addFilter(bloom);
      
        viewPort.addProcessor(fpp);
        
        
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);
        viewPort.setBackgroundColor(ColorRGBA.fromRGBA255(0, 0, 128, 1));
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0f, -90f, -65)); 
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        cam.setLocation(new Vector3f(0f, 0f, 15f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
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
        centerObject.setLocalTranslation(0, 0, 0);
        centerObject.move(0, -5, 0);

        // Colisiones
        SphereCollisionShape collisionShape = new SphereCollisionShape(5f);
        RigidBodyControl control = new RigidBodyControl(collisionShape, 0);
        centerObject.addControl(control);
        control.setPhysicsLocation(centerObject.getLocalTranslation());

        
        rootNode.attachChild(centerObject);
        centerObject.attachChild(clownfish);
        clownfish.setLocalTranslation(4, 4, 0);
        
        Node reef = (Node) assetManager.loadModel("Models/reef/reef.j3o");
        reef.scale(5f);
        reef.move(0, -6, 0);
        rootNode.attachChild(reef);
    }
    
    private void LoadEnemis(){
        Node enemyModel = (Node) assetManager.loadModel("Models/anglerfish.j3o");
        
        for(int i=0; i<5; i++){
            // Define el rango de coordenadas para la aparición del enemigo
            float minX = -15f; // Límite izquierdo de la pantalla
            float maxX = 15f;  // Límite derecho de la pantalla
            float minY = 0f; // Límite inferior de la pantalla
            float maxY = 15f;  // Límite superior de la pantalla

            // Calcula una posición aleatoria dentro del rango definido
            float randomX = FastMath.nextRandomFloat() * (maxX - minX) + minX;
            float randomY = FastMath.nextRandomFloat() * (maxY - minY) + minY;
        
            Vector3f spawnPosition = new Vector3f(randomX, randomY, 0);
            Node clonedEnemyModel = (Node) enemyModel.clone();

            Box collisionShape = new Box(1, 1, 1);
            Geometry collisionGeometry = new Geometry("Enemy", collisionShape);
            Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", new ColorRGBA(1, 1, 1, 0)); // Establece el color con transparencia
            material.setFloat("AlphaDiscardThreshold", 0.5f); // Umbral de descarte alfa
            collisionGeometry.setQueueBucket(RenderQueue.Bucket.Transparent); // Asegura que se renderice correctamente
            collisionGeometry.setMaterial(material);
            collisionGeometry.setLocalTranslation(clonedEnemyModel.getLocalTranslation());
            
            clonedEnemyModel.attachChild(collisionGeometry);
            
            Enemy anglerfish = new Enemy(clonedEnemyModel, spawnPosition, centerObject, 1.0f, this);
            anglerfish.setLocalScale(0.2f);
            
            clonedEnemyModel.updateModelBound();
            
            rootNode.attachChild(anglerfish);
        }
    }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_CLICK) && !isPressed) {
                CollisionResults results = new CollisionResults();
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 0f);
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 1f).subtractLocal(click3d);
                Ray ray = new Ray(click3d, dir);
                rootNode.collideWith(ray, results);
                if (results.size() > 0){
                    Geometry target = results.getClosestCollision().getGeometry();
                    if(target.getName().equals("Enemy")){
                        Spatial enemySpatial = target.getParent();
                        
                        ParticleEmitter emitter = createParticleEmitter();
                        emitter.setLocalTranslation(enemySpatial.getWorldTranslation());
                        rootNode.attachChild(emitter);
                        
                        rootNode.detachChild(enemySpatial);
                        enemySpatial.removeFromParent();
                        SpawnEnemy();
                        updateScore();
                    }
                }
            }
        }
    };
    
    private ParticleEmitter createParticleEmitter() {
        ParticleEmitter emitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", assetManager.loadTexture("Textures/bubble_1.png"));
        emitter.setMaterial(mat_red);
        
        emitter.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f)); // Color de las partículas al final
        emitter.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // Color de las partículas al inicio
        emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        emitter.setStartSize(1.0f);
        emitter.setEndSize(0.1f);
        emitter.setGravity(0, -1, 0);
        emitter.setLowLife(1f);
        emitter.setHighLife(2f);
        emitter.getParticleInfluencer().setVelocityVariation(0.3f);
        return emitter;
    }

    
    private final ActionListener actionListenerReset = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if  (name.equals(MAPPING_RESTART) && !isPressed) {
                restartGame(); // Llama al método para reiniciar el juego
            }
        }
    };
    
    


    private void SpawnEnemy(){
        Node enemyModel = (Node) assetManager.loadModel("Models/anglerfish.j3o");
        
        // Define el rango de coordenadas para la aparición del enemigo
        float minX = -15f; // Límite izquierdo de la pantalla
        float maxX = 15f;  // Límite derecho de la pantalla
        float minY = 15f; // Límite inferior de la pantalla
        float maxY = 15f;  // Límite superior de la pantalla

        // Calcula una posición aleatoria dentro del rango definido
        float randomX = FastMath.nextRandomFloat() * (maxX - minX) + minX;
        float randomY = FastMath.nextRandomFloat() * (maxY - minY) + minY;
    
        Vector3f spawnPosition = new Vector3f(randomX, randomY, 0);
        Node clonedEnemyModel = (Node) enemyModel.clone();
        
        Box collisionShape = new Box(1, 1, 1);
        Geometry collisionGeometry = new Geometry("Enemy", collisionShape);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", new ColorRGBA(1, 1, 1, 0)); // Establece el color con transparencia
        material.setFloat("AlphaDiscardThreshold", 0.5f); // Umbral de descarte alfa
        collisionGeometry.setQueueBucket(RenderQueue.Bucket.Transparent); // Asegura que se renderice correctamente
        collisionGeometry.setMaterial(material);
        collisionGeometry.setLocalTranslation(clonedEnemyModel.getLocalTranslation());
        clonedEnemyModel.attachChild(collisionGeometry);
        Enemy anglerfish = new Enemy(clonedEnemyModel, spawnPosition, centerObject, 1.0f, this);
        anglerfish.setLocalScale(0.2f);
        clonedEnemyModel.updateModelBound();
        rootNode.attachChild(anglerfish);
    }
    
    
    @Override
    public void simpleUpdate(float tpf) {
        RotateClownFish(tpf);
        for (Spatial node : rootNode.getChildren()) {
            if (node instanceof Enemy) {
                ((Enemy) node).moveToTarget(tpf);
            }
        }
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
    
    private void loadAndPlayMusic() {
        // Cargar el archivo de audio 
        audio = new AudioNode(assetManager, "Sounds/AquaSong.wav", DataType.Stream);
        audio.setLooping(true); // Para que la música se repita
        audio.setPositional(false);
        audio.setVolume(8); // Ajusta el volumen

        // Adjuntar el nodo de audio al nodo raíz
        rootNode.attachChild(audio);
        audio.play();
    }
    
    private void initScoreText() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize());
        scoreText.setColor(ColorRGBA.White);
        scoreText.setText("Score: " + score);
        scoreText.setLocalTranslation(300, scoreText.getLineHeight(), 0);
        guiNode.attachChild(scoreText);
    }
    
    public void initHealthText() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        healthText = new BitmapText(guiFont, false);
        healthText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        healthText.setColor(ColorRGBA.Red);                             // font color
        healthText.setText("Health: " + anemoneHealth);                 // the text
        healthText.setLocalTranslation(300, healthText.getLineHeight() * 2, 0); // position
        guiNode.attachChild(healthText);
    }

    public void updateHealth() {
        healthText.setText("Health: " + anemoneHealth);
        if (anemoneHealth <= 0) {
            showGameOver();
        }
    }
    
    private void updateScore() {
        score += 10;
        scoreText.setText("Score: " + score);
    }

    private void showGameOver() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        gameOverText = new BitmapText(guiFont, false);
        gameOverText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        gameOverText.setColor(ColorRGBA.Red);
        gameOverText.setText("Game Over!");
        gameOverText.setLocalTranslation(300, cam.getHeight() / 2, 0);
        guiNode.attachChild(gameOverText);

        // Detener el juego, opcional
        stopGame();
    }

    private void stopGame() {
        // Detiene las actualizaciones del juego
        // Por ejemplo, deshabilitar el ActionListener
        inputManager.removeListener(actionListener);
        clownfish.removeFromParent();
    }
    
    // Método para reiniciar el juego
    private void restartGame() {
        score = 0;
        anemoneHealth = 300;
        LoadEnemis();

        // Elimina el texto "Game Over" si existe
        if (gameOverText != null) {
            guiNode.detachChild(gameOverText);
            gameOverText.removeFromParent();
            gameOverText = null;
        }

        // Vuelve a activar el ActionListener para las interacciones del jugador
        inputManager.addListener(actionListener, new String[]{MAPPING_CLICK});
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
