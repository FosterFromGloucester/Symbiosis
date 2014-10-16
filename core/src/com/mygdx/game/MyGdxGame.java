//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014


package com.mygdx.game;

import java.util.ArrayList;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class MyGdxGame extends ApplicationAdapter{
	
	SpriteBatch batch;
	int screenWidth;
	int screenHeight;
	int[][] map;//underlying grid
	int mapWidth = 400;
	int mapHeight = 35;
	int tileSize = 20;
	Texture tileTexture;
	
	TiledMap tilemap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;
	
	Texture backgroundImage;
	
	float offsetX =0f;
	float offsetY =0f;
	
	int lastSaveX;
	int lastSaveY;
	
	int furthestSaveX;
	int furthestSaveY;
	
	ProgressBar HealthBar;
	int XP;
	
	NinePatchDrawable loadingBarBackground;
	NinePatchDrawable loadingBar;
	
	ArrayList<PowerUp> powerUps  =new ArrayList<PowerUp>();//array of powerups yet to be implmented
	ArrayList<Entity> entities = new ArrayList<Entity>();//array of entities holding both enemies and the player
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();//array of bullets that have been fired
	
	enum Axis { X, Y };
	public enum Direction { U, D, L, R };
	
	public Player thePlayer;
	
	

  @Override
  public void create () {
	  batch = new SpriteBatch();
	  tileTexture = new Texture("block.png");  
	  
	  setUpMapAndCamera();
	  addPlayers();//add the players
	  //loadFromSave();
	  addEnemies();
	  addBoss();
	  
	  Music music = Gdx.audio.newMusic(Gdx.files.internal("Forest.mp3"));
	  music.setVolume(0.1f); // sets the volume to half the maximum volume
	  music.setLooping(true);
	  music.play();
	  
	  new Texture("Switchactivated.png");
  }
  
  private void addPlayers(){
	  thePlayer = new Player(this, screenWidth/2, screenHeight/2, 47, 39, 120.0f, new Texture("Alien.png"),"Alien.png");
	  entities.add(thePlayer);//single player is created
  }
  
  public void moveEntity(Entity e, float newX, float newY) {
	  // just check x collisions keep y the same
	  moveEntityInAxis(e, Axis.X, newX, e.y);
	  // just check y collisions keep x the same
	  moveEntityInAxis(e, Axis.Y, e.x, newY);
  }
  
  public void moveEntityInAxis(Entity e, Axis axis, float newX, float newY) {
	  Direction direction;
	  
	  // determine axis direction
	  if(axis == Axis.Y) {
		  if(newY - e.y < 0) direction = Direction.U;
		  else direction = Direction.D;
	  }
	  else {
		  if(newX - e.x < 0) direction = Direction.L;
		  else direction = Direction.R;
	  }
	  
	  boolean entity = true;
	  boolean tile = true;
	  
	  if(e instanceof Bullet){
		  tile = !bulletCheck(e, direction, newX, newY);
	  }
	  else{
		  tile = !tileCollision(e, direction, newX, newY);
		  entity = !entityCollision(e, direction, newX, newY);
	  }
	  

	  if(tile && entity ) {
		  if( newX > e.x && e instanceof Player){
			  if((newX+e.width+screenWidth/2) < mapWidth*tileSize && offsetX>=0 ){
				  camera.translate(newX-e.x,0);
			  }
			  else{
				  offsetX=(offsetX+(newX-e.x));
			  }
		  }
		  else if(newX < e.x  && e instanceof Player){
			  if((newX-screenWidth/2)>0 && offsetX<=0 ){
				  camera.translate(-1*(e.x-newX),0);  
			  }
			  else{
				  offsetX=(offsetX-(e.x-newX)) ;
			  }
		  }
		  e.move(newX, newY);
	  }
	  else if (!entity){
		  
	  }
	  // else collision with wither tile or entity occurred 
  }
  
  public boolean tileCollision(Entity e, Direction direction, float newX, float newY) {
	  boolean collision = false;

	  // determine affected tiles
	  int x1 = (int) Math.floor((Math.min(e.x, newX))/ tileSize);
	  int y1 = (int) Math.floor((Math.min(e.y, newY))/ tileSize);
	  int x2 = (int) Math.floor((Math.max(e.x, newX) + e.width - 0.1f) / tileSize)-1;
	  int y2 = (int) Math.floor((Math.max(e.y, newY) + e.height - 0.1f) / tileSize);
	  
	  //System.out.println("Left = "+x1+" Right = "+x2+" Top = "+y2+" Bottom = "+y1);
	// tile checks
		  for(int x = x1; x <= x2; x++) {
			  for(int y = y1; y <= y2; y++) {
				  if(map[x][y] == 1) {
					  collision = true;	
					  e.tileCollision(map[x][y], x, y, newX, newY, direction);
					  break;
				  }
				  else if(map[x][y]==2){
					  if(e instanceof Player){
						  checkSentries();
					  }
				  }
				  else if(map[x][y]==3){
					  lastSaveX = x;
					  lastSaveY = y;
				  }
				  else if(map[x][y]==4){
					  thePlayer.setBatTexture();
					  Player.batState = true;
				  }
				  else if(map[x][y]==5){
					  thePlayer.Alive = false;
				  }
				  else if(map[x][y]==6){
					  entities.add(new Entity(this, x*tileSize, y*tileSize, 20, 60, 0, new Texture("Switchactivated.png"), "Switchactivated.png"));
					  deactivateSentries();
				  }
			  }
		  }
	  return collision;
  }
  

public boolean entityCollision(Entity e1, Direction direction, float newX, float newY) {//this function checks if any entities have collidied
	 
	  boolean collision = false;
	  
	  for(int i = 0; i < entities.size(); i++) {//go through all the entities
		  Entity e2 = entities.get(i);
		  
		  if(e1 != e2) {
			  // axis aligned rectangle rectangle collision detection
			  if(newX < e2.x + e2.width && e2.x < newX + e1.width && newY < e2.y + e2.height && e2.y < newY + e1.height) {
				  boolean check = BitCheckOptimised(e1,e2);//once the bounding box has collided go to the pixel level tests
				  collision = check;
				  if(check == true){
					  e1.entityCollision(e2,e1, newX, newY, direction);//output the collision details
					  break;//break increases efficiency
				  }
			  }
		  }
	  }
	  return collision;
  }

  @Override
  public void render () {
	  
	  float delta = Gdx.graphics.getDeltaTime();
	  for(int i = entities.size() - 1; i >= 0; i--) {
		  Entity e = entities.get(i);
		  boolean check = e.update(delta);
		  if(check){//move the entity only when the user has pressed the arrow keys
			  moveEntity(e, e.x + e.dx, e.y + e.dy); 
		  }
		  // now we try move the entity on the map and check for collisions
	  }	 

	  if(thePlayer.Alive == false){
		  System.out.println("Reviving");
		  revivePlayer(thePlayer.getGunState());
	  }
	  
	  Gdx.gl.glClearColor(0, 0, 0, 1);
	  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	  
	  camera.update();
	  batch.setProjectionMatrix(camera.combined);
	  tiledMapRenderer.setView(camera);
	  tiledMapRenderer.render();
	  batch.begin();
	  
//	  ArrayList<TileNode> thePath = CaveBoss.thePath;
//	  
//	  for(int i = 0;i<thePath.size();i++){
//		  	if(CaveBoss.BossBrain.getCurrentState().equals("Attack"))
//			batch.draw(CaveBoss.futureTexture,CaveBoss.thePath.get(i).x*tileSize, CaveBoss.thePath.get(i).y*tileSize); 
//	  
//	  }

	  // draw all entities
	  for(int i = entities.size() - 1; i >= 0; i--) {
		  Entity e = entities.get(i);
		  if(e instanceof Player){
			   e = (Player) entities.get(i);
			   batch.draw(((Player) e).getTexture(), e.x, e.y-(e.offsetDown));  
		  }
		  else if(e instanceof CaveBoss){
			  e = (CaveBoss) entities.get(i);
			  batch.draw(e.texture, e.x, e.y-(e.offsetDown));
		  }
//		  else if(e instanceof CaveBoss){
//			  batch.draw(e.texture, e.x, e.y-(e.offsetDown));
//			  for(int j = 0;j<((CaveBoss) e).thePath.size();j++){
//					batch.draw(((CaveBoss) e).futureTexture,((CaveBoss) e).thePath.get(i).x*tileSize, ((CaveBoss) e).thePath.get(i).y*tileSize); 
//			  }
//		  }
		  else{
			  batch.draw(e.texture, e.x, e.y-(e.offsetDown)); //ensure the sprite is flush with the floor 
		  }
	  }
	  
	  //draw the bullets
	  for(int j=0;j<bullets.size();j++){
		  Bullet b = bullets.get(j);
		  if(b.update(delta)){
			  moveEntity(b, b.x + b.dx, b.y + b.dy); 
		  }
	  }

	  for(int i = bullets.size() - 1; i >= 0; i--) {
		  Bullet b = bullets.get(i);
		  batch.draw(b.texture, b.x, b.y); //draw the bullet
	  }

	  batch.end();
  }

  private boolean BitCheckOptimised(Entity e1,Entity e2) {//Quicker method of pixel level testing as bitmask lookup is o(1)
	  	boolean checker = false;
	
		int[][] bitmask1 = e1.getBitmask();//get the bitmask for each sprite that was created on the creation of the sprite
		int[][] bitmask2 = e2.getBitmask();
		
		//e1.printBitmask();
		//e2.printBitmask();
		
		//calculate box of intersection
		int left = (int) Math.max(e1.x, e2.x);
		int right = (int) Math.min(e1.x+e1.width, e2.x+e2.width);
		int top = (int) Math.min(e1.y+e1.height, e2.y+e2.height);
		int bottom = (int) Math.max(e1.y, e2.y);
		
//		System.out.println("Left = "+left+" Right = "+right+" Top = "+top+" Bottom = "+bottom);
		
		int dif = top - bottom;
//		System.out.println(dif);
		
		for (int x = dif-1; x >= 0; x--) {//go through every row of the box of intersection top to bottom
				 
				  int e1Shift = Math.abs((int) (left - e1.x));
				  int e2Shift = right-left;//shift the bitmask
//				  System.out.println("e1.x: "+e1.x);
				  checker = checkRow(bitmask1[x],bitmask2[x],e1Shift,e2Shift);//check if the rows have colliding pixels 
				  if(checker == true){
					  break;
				  }
		}
		return checker;
	}
  
  public boolean checkRow(int[] e1,int[] e2,int shift,int duration){//this method compares the rows of the sprites to see if any collision occurs
	  boolean checker = false;
	  for(int i = 0;i<duration;i++){
		  if(e1[shift+i] == 1 && e2[i] == 1){//if true collision occurs
			  checker = true;
			  break;
		  }
	  }
	  return checker;
  }
  
  public boolean bulletCheck(Entity e, Direction direction, float newX,float newY){ //simple function used to check whether the block has hit an entity or collided with a tile
	  boolean collision = false;
	  
	  int x1 = (int) Math.floor((Math.min(e.x, newX))/ tileSize);
	  int x2 = (int) Math.floor((Math.max(e.x, newX) + e.width - 0.1f) / tileSize)-1;
	  int y2 = (int) Math.floor((Math.max(e.y, newY) + e.height - 0.1f) / tileSize);
	  int y1 = (int) Math.floor((Math.min(e.y, newY))/ tileSize);
	  
	  for(int i = 0; i < entities.size(); i++) {//go through all the entities
		  Entity e2 = entities.get(i);
		  
		  if(e != e2) {
			  // axis aligned rectangle rectangle collision detection
			  if(newX < e2.x + e2.width && e2.x < newX + e.width && newY < e2.y + e2.height && e2.y < newY + e.height) {
				  boolean check = BitCheckOptimised(e,e2);//once the bounding box has collided go to the pixel level tests
				  collision = check;
				  if(check == true){
					  e.entityCollision(e2,e,newX, newY, direction);//output the collision details
					  break;//break increases efficiency
				  }
			  }
		  }
	  }
	  
	  if(x2>=mapWidth){//if off screen
		bullets.remove(e);
		return true;
	  }
	  else if(x2< 0){
		bullets.remove(e);
		return true;
	  }
	  else{
	  // tile checks
		  for(int x = x1; x <= x2; x++) {
			  for(int y =y1 ; y <= y2; y++) {
				  if(map[x][y] == 1) {
					  collision = true;	
					  if(e instanceof Bullet ){
						  bullets.remove(e);
					  }
					  break;
				  }
			  }
		  }
	  return collision;
	  }
  }
  
  public void setUpMapAndCamera(){//This function sets up the map by loading and setting the layers to adjust the 2D underlying grid
	  
	  screenWidth = Gdx.graphics.getWidth();
	  screenHeight = Gdx.graphics.getHeight();
	  
	  camera = new OrthographicCamera();
	  camera.setToOrtho(false,screenWidth,screenHeight*2);
	  camera.update();
	  
	  tilemap = new TmxMapLoader().load("StartLevelTest.tmx");
	  tiledMapRenderer = new OrthogonalTiledMapRenderer(tilemap);
	   	  
	  TiledMapTileLayer layer = (TiledMapTileLayer) tilemap.getLayers().get("Collidable");
	  TiledMapTileLayer PowerUplayer = (TiledMapTileLayer) tilemap.getLayers().get("PowerUps");
	  TiledMapTileLayer Triggerlayer = (TiledMapTileLayer) tilemap.getLayers().get("Triggers");
	  TiledMapTileLayer Gunlayer = (TiledMapTileLayer) tilemap.getLayers().get("SentriesLeft");
	  TiledMapTileLayer SavePointLayer = (TiledMapTileLayer) tilemap.getLayers().get("SavePoints");
	  TiledMapTileLayer batLayer = (TiledMapTileLayer) tilemap.getLayers().get("Bat");
	  TiledMapTileLayer GunLayerRight = (TiledMapTileLayer) tilemap.getLayers().get("SentriesRight");
	  TiledMapTileLayer SwitchesLayer = (TiledMapTileLayer) tilemap.getLayers().get("Switch");
	  TiledMapTileLayer thornsLayer = (TiledMapTileLayer) tilemap.getLayers().get("Thorns");
	  
	   
	  convertTiledMap(layer);
	  addPowerUps(PowerUplayer);
	  addTriggers(Triggerlayer);
	  addGunsLeft(Gunlayer);
	  addSavePoints(SavePointLayer);
	  addBats(batLayer);
	  addGunsRight(GunLayerRight);
	  addSwitches(SwitchesLayer);
	  addThorns(thornsLayer);
	  
	  
  }
  
  private void addThorns(TiledMapTileLayer layer) {
	  int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					map[i][j] = 5;
				}
			}
		}
}

  private void addSwitches(TiledMapTileLayer layer) {
	int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					map[i][j] = 6;
				}
			}
		}
}

  private void addBats(TiledMapTileLayer layer) {
	  int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					map[i][j] = 4;
					entities.add(new PowerUp(this, i*tileSize, j*tileSize, 30, 12, new Texture("Bat.png"),"Bat.png"));
				}
			}
		}
}

  public void convertTiledMap(TiledMapTileLayer layer){
	  int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  map = new int[rows][columns]; 
		for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					map[i][j] = 1;
				}
				else{
					map[i][j] = 0;
				}
			}
		}
  }
  
  public void addPowerUps(TiledMapTileLayer layer){
	  int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					entities.add(new PowerUp(this, i*tileSize, j*tileSize, 20, 20, new Texture("enemy.png"),"enemy.png"));
				}
			}
		}
  }
   
  public void addGunsLeft(TiledMapTileLayer layer){
	  int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					entities.add(new SentryGun(this, i*tileSize, j*tileSize, 45, 43, 0, new Texture("SentryGun.png"),"SentryGun.png", false));
				}
			}
		}
  }
  
  public void addGunsRight(TiledMapTileLayer layer){
	  int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					entities.add(new SentryGun(this, i*tileSize, j*tileSize, 45, 43, 0, new Texture("SentryGunRight.png"),"SentryGunRight.png", true));
				}
			}
		}
  }
  
  public void addSavePoints(TiledMapTileLayer layer){
	  int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					map[i][j] = 3;
					//camera.position.x = i;
					//camera.position.y = j;
					furthestSaveX = i;
					furthestSaveY = j;
				}
			}
		}
  }
  
  public void addTriggers(TiledMapTileLayer layer){
	  int rows = layer.getWidth();
	  int columns = layer.getHeight();
	  
	  for(int i = 0;i<rows;i++){
			for(int j = 0; j<columns;j++){
				if(layer.getCell(i,j) != null ){
					map[i][j] = 2;
				}
			}
		}
  }
  
  @Override
  public void resize(int width, int height) {
      camera.viewportWidth = width;
      camera.viewportHeight = height;
      camera.position.set(width/2f, height/2f, 0); //by default camera position on (0,0,0)
  }
  
  public void addEnemies(){
	 entities.add(new Enemy(this, 180*tileSize, 10*tileSize,44, 58, 30.0f, new Texture("Penguin.png"),"Penguin.png")); 
	 entities.add(new Enemy(this, 250*tileSize, 6*tileSize,44, 58, 30.0f, new Texture("Penguin.png"),"Penguin.png")); 
	 entities.add(new Enemy(this, 284*tileSize, 6*tileSize,44, 58, 30.0f, new Texture("Penguin.png"),"Penguin.png")); 
  }
  
  public boolean wallCheck(Entity e, float newX, float newY) {//this function is used by the enemies to see whether they have collided with a wall so they can change direction
	  boolean collision = false;

	  // determine affected tiles
	  int x1 = (int) Math.floor((Math.min(e.x, newX))/ tileSize);
	  int y1 = (int) Math.floor((Math.min(e.y, newY))/ tileSize);
	  int x2 = (int) Math.floor((Math.max(e.x, newX) + e.width - 0.1f) / tileSize)-1;
	  int y2 = (int) Math.floor((Math.max(e.y, newY) + e.height - 0.1f) / tileSize);
	  
	  for(int y = y1; y <= y2; y++) {
//		  System.out.println("y "+y);
//		  System.out.println("x1 "+x1);
//		  System.out.println("x2 "+x2);
		  if(map[x1][y] == 1|| map[x2][y]==1) {
				collision = true;	
				break;
		  }
	  }

	  return collision;	  
  }
  
  public void revivePlayer(int previousGunState){//if the player has died create a new player at the last checkpoint
	  entities.remove(thePlayer);
	  Player newPlayer = new Player(this, lastSaveX*tileSize, lastSaveY*tileSize, 47, 39, 120.0f, new Texture("Alien.png"),"Alien.png");
	  thePlayer = newPlayer;
	  entities.add(newPlayer);//single player is created
	  newPlayer.setGunState(previousGunState);
	  int x = lastSaveX*tileSize;
	  camera.position.set(x, camera.viewportHeight/2f, 0); //by default camera position on (0,0,0)
	  bullets.clear();
  }
  
  public void checkSentries(){//checks to see whether the sprite is in range
		for(int i = 0; i<entities.size();i++ ){
			Entity e = entities.get(i);
			 if(e instanceof SentryGun){
				 e = entities.get(i);
				 if(Math.abs(((SentryGun) e).y-thePlayer.y)<40){
					 ((SentryGun) e).setActive(true);
					 ((SentryGun) e).Fire();
				 }
				 else{
					 ((SentryGun) e).setActive(false); 
				 }
				 
			  }
		}
	}
  
  private void deactivateSentries() {
	  for(int i = 0; i<entities.size();i++ ){
			Entity e  = entities.get(i);
			 if(e instanceof SentryGun){
				 e = entities.get(i);
				((SentryGun) e).switchedOff= true; 
			}
	   }
  }
  
  public void addBoss(){
	  entities.add(new CaveBoss(this, 338*tileSize,28*tileSize,100 , 80, 40f, new Texture("Monster.png"),"Monster.png"));
  }
  
  public void loadFromSave(){
	  Player newPlayer = new Player(this, lastSaveX*tileSize, lastSaveY*tileSize, 47, 39, 120.0f, new Texture("Alien.png"),"Alien.png");
	  thePlayer = newPlayer;
	  entities.add(newPlayer);//single player is created
	  int x = furthestSaveX*tileSize;
	  camera.position.set(x, camera.viewportHeight/2f, 0); //by default camera position on (0,0,0)
	  bullets.clear();
	  thePlayer.setGunState(1);
  }
  
  
  
}
