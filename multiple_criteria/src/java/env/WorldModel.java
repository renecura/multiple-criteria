package env;

import jason.environment.grid.Location;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class WorldModel {

	private static WorldModel instance = null;
	
	// Componentes del modelos.
	private CopyOnWriteArrayList<WorldAgent> agents;
		
	// Mapeo de atributos del modelo.
	private int nextAgId = 0;
	
	private Map<String,Integer> names;
	
	private ArrayList<Location> freeCells;
	private ArrayList<Location> agentCells;
	
	// Grilla
	private WorldAgent[][] grid = null; 
	private int neighborhood = 1;
	

	// Alto y ancho en celdas
	private int width;
	private int height;
	
	// Logger
	private Logger logger = Logger.getLogger("Model:"+WorldModel.class.getName());
	
	// Constructor
	private WorldModel() {
		
		int i, j;
		
		Properties prop = new Properties();
		
    	try {
    		//load a properties file
    		prop.load(new FileInputStream("config.properties"));
    	
    		// Determina el tama�o del modelo.
        	width = Integer.parseInt(prop.getProperty("worldwidth"));
    		height = Integer.parseInt(prop.getProperty("worldheight"));
    		
    	} catch (IOException ex) {
    		logger.warning("Archivo de propiedades no encontrado, se utilizar�n valores por defecto. "+ex.getMessage());
    		width = 64;
    		height = 64;
        }
    	
    	// Crea el conjunto de agentes, como m�ximo el n�mero de casillas del modelo.
		agents = new CopyOnWriteArrayList<WorldAgent>();
		names = new HashMap<String,Integer>(width * height);
		
		// Inicializa las listas de celdas.
		agentCells = new ArrayList<Location>();
		freeCells = new ArrayList<Location>();
		
		for(i = 0; i < width; i++)
			for(j = 0; j < height; j++)
				freeCells.add(new Location(i,j));
		
		// Inicializa la grilla
		grid = new WorldAgent[width][height];

		
		logger.info("Parameters -> width: "+this.width+"height:"+this.height);
		
	}
	
	// Obtiene la instancia del modelo.
	public static WorldModel getInstance(){

		if(instance == null) {
	         instance = new WorldModel();
	      }

		return instance;
	}
		
	// Agrega un agente al cunjutno de agentes
	public void addAgent(WorldAgent ag) {
		agents.add(ag);
		Location l = ag.getLocation();
		grid[l.x][l.y] = ag;
	}
	
	// Asocia un nombre con un agente
	public void nameAgent(String agName) {
		if(!names.containsKey(agName))
			names.put(agName, nextAgId++);
	}
	
	// Retorna el agente en base a su nombre
	public WorldAgent getAgent(String agName) {
		Integer ag = names.get(agName);
		return agents.get(ag);
	}

	public Location getFreeLocation(){
		int r = (int)(Math.random() * freeCells.size());
		
		return freeCells.remove(r);
	}
	
	public Iterator<WorldAgent> getNeighbors(WorldAgent ag){
		int i;
		ArrayList<WorldAgent> list = new ArrayList<WorldAgent>();
		Location l = ag.getLocation();
		
		for(i = 1; i <= neighborhood; i++ ){
			
			if (l.y-i >= 0 	   && grid[l.x][l.y-i] != null) list.add(grid[l.x][l.y-i]);
			if (l.y+i < height && grid[l.x][l.y+i] != null) list.add(grid[l.x][l.y+i]);
			if (l.x-i >= 0     && grid[l.x-i][l.y] != null) list.add(grid[l.x-i][l.y]);
			if (l.x+i < width  && grid[l.x+i][l.y] != null) list.add(grid[l.x+i][l.y]);
			
		}
				
		//if(!list.isEmpty()) logger.info("Ag: "+ag.toString()+" -> Vencinos: "+list);
		
		return list.iterator();
	}
	
	// recupera el iterador de agentes.
	public Iterator<WorldAgent> getAgentsIterator() {
		return this.agents.iterator();
	}

	// Getters and setters
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}
