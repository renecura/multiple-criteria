package env;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glColor3ub;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.environment.grid.Location;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import ag.MultipleCriteriaAgent;

public class WorldView implements Runnable{

	private final WorldModel model;
	
	// Tamaño de la celda.
	private final int cell_size;
	
	// Alto y ancho de la pantalla.
	private final int view_hsize;
	private final int view_vsize;
	
	// Logger
	private Logger logger = Logger.getLogger("View:"+WorldView.class.getName());
	
	// Constructor.
	public WorldView(WorldModel model){
		
		this.model = model;
		
		int size;
		
		Properties prop = new Properties();
		
    	try {
    		//load a properties file
    		prop.load(new FileInputStream("config.properties"));
    	
    		// Determina el tamaño de la vista.
        	size = Integer.parseInt(prop.getProperty("viewsize"));
    		
    	} catch (IOException ex) {
    		logger.warning("Archivo de propiedades no encontrado, se utilizarán valores por defecto. "+ex.getMessage());
    		size = 640;
        }
		
		// Crea la vista del modelo.
		//view = new WorldView(this, viewsize);
		
		this.cell_size = size / model.getWidth();
		
		this.view_hsize = model.getWidth() * this.cell_size;
		this.view_vsize = model.getHeight() * this.cell_size;
		
		logger.info("Cell Size: "+this.cell_size+" hsize: "+this.view_hsize+", "+this.view_vsize);
		
	}
	
	// Inicia la ejecución de la vista.
	public void start(){
		Thread t = new Thread(this);
		t.start();
	}
	
	// Inicializa los componentes de la vista.
	public void init(){
		
		try {
		    Display.setDisplayMode(new DisplayMode(this.view_hsize,this.view_vsize));
		    Display.setVSyncEnabled(true);
		    Display.create();		    
		    Display.setTitle("Multiple Criteria");
		} catch (LWJGLException e) {
		    e.printStackTrace();
		    System.exit(0);
		}
		
		// init OpenGL
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, this.view_hsize, 0, this.view_vsize, 1, -1);
		glMatrixMode(GL_MODELVIEW);		
	}
	
	// Renderiza la vista y todos los componentes en el modelo.
	public void render(){
		
		// Recorre los agentes y los renderiza.
		glColor3f(1.0f, 0.0f, 0.0f);
		Iterator<MultipleCriteriaAgent> ait = model.getAgentsIterator();
		
		while(ait.hasNext())
			renderAgent(ait.next());
		
		// Actualiza la vista.
		Display.update();
		Display.sync(60);
	}
	
	// Dibuja un agente. Método por defecto.
	public void renderAgent(MultipleCriteriaAgent ag){
		int slices = 8; // Cantidad de porciones que generarán el circulo.
		int color = 0;
		
		if (ag == null) return;
		
		Location l = ag.getLocation();
		
		glPushMatrix();
		glTranslatef((float)((l.x+0.5)*this.cell_size), (float)((l.y+0.5)*this.cell_size), 0);
		glScalef(this.cell_size/2-1, this.cell_size/2-1, 1);
		
		// Establece el color del agente segun su preferencia personal.
		Iterator<Literal> it = ag.getBB().getCandidateBeliefs(Literal.parseLiteral("chosen_alt(_)"), null);
		
		if (it != null && it.hasNext())
		color = (int)(((NumberTerm)(it.next().getTerm(0))).solve());
		else logger.info("Ag "+ag+" no tiene belief");
		
		byte r, g, b;
		
		if (color == 0) {
			r = (byte)128;
			g = (byte)128;
			b = (byte)128;
		}
		else {
			r = (byte)(((color & 0x04) > 0)? 255: 0);
			g = (byte)(((color & 0x02) > 0)? 255: 0);
			b = (byte)(((color & 0x01) > 0)? 255: 0);
		}
		
		glColor3ub(r, g, b);
		
		glBegin(GL_TRIANGLE_FAN);
		
		glVertex2f(0, 0);		
		for(int i = 0; i <= slices; i++){ 
		    double angle = Math.PI * 2 * i / slices;
		    glVertex2f((float)Math.cos(angle), (float)Math.sin(angle));
		}
		
		glEnd();
		
		glPopMatrix();
	}

	public void run() {
		this.init();
		
		while(!Display.isCloseRequested())
		render();
		
		Display.destroy();
	}
		
}
