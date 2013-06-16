package thutconcrete.common.utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockDust;
import thutconcrete.common.blocks.BlockLava;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ExplosionCustom
{

	public static final double sqrt3 = Math.sqrt(3.0D);
	public static double pi = 3.141592653589793D;
	public static double sqrt2 = Math.sqrt(2.0D);
	static LinearAlgebra vec;
	public static final int MAX_RADIUS = 128;
	public static final int DUSTID = BlockDust.instance.blockID;
	
	  public static void doExplosion(World worldObj,double x0, double y0, double z0, double r0, boolean vapourize)
	  {
		    double A = 128;
		    double B = 0.1;
		    short rMax = (short)Math.min(Math.sqrt(A * r0 / B),MAX_RADIUS);
		    double[] centre = { x0, z0, y0 };
		    if(!worldObj.isRemote)
		    sphericalExplosion(worldObj, x0, z0, y0, r0, vapourize, A, B, rMax);    
		    
	  }
	  
	  public static void doExplosion(World worldObj,double x0, double y0, double z0, double r0, boolean vapourize, short rMax)
	  {
		    double A = 128;
		    double B = 0.1;
		    double[] centre = { x0, z0, y0 };
		    if(!worldObj.isRemote)
		    sphericalExplosion(worldObj, x0, z0, y0, r0, vapourize, A, B, rMax);    
		    worldObj.createExplosion(null, x0, y0, z0, (float) r0, false);
	  }

	  public static synchronized void sphericalExplosion(final World worldObj,final double x0,final double y0,final double z0, final double r0,final boolean vapourize, final double A, final double B, final int rMax)
	  {
	    Thread nextBoom = new Thread(new Runnable() {
	      public void run() { 
	        final Double[] centreD = { x0, y0, z0 };

	        double[] centre = { x0, y0, z0 };
	        
	        ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();

	        Cruncher sorter = new Cruncher();
	        
	        final List<Integer[]> things = new ArrayList();
	        final List<Integer[]> stuff = new ArrayList();

	        final List<Integer[]> morethings = new ArrayList();
	        final List<Integer[]> morestuff = new ArrayList();
	        
	        final List<Integer[]> otherthings = new ArrayList();
	        final List<Integer[]> otherstuff = new ArrayList();

	        final List<Integer[]> moreotherthings = new ArrayList();
	        final List<Integer[]> moreotherstuff = new ArrayList();
	        
	        long startTime = System.nanoTime();
	        
	        
	        
	      //*/
	        final CountDownLatch latch = new CountDownLatch(4);
	        
	        Thread part1 = new Thread(new Runnable() {public void run() {
	   //     	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	        	Cruncher.destroyInRangeV2(things, centreD, worldObj, A, rMax, r0, rMax, rMax, 0, rMax, rMax, 0, vapourize);
	        	latch.countDown();
	        	
	        }});
	        Thread part2 = new Thread(new Runnable() {public void run() {
	    //    	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	        	Cruncher.destroyInRangeV2(stuff, centreD, worldObj, A, rMax, r0, rMax, rMax, rMax, 0, rMax, 0, vapourize);
	        	latch.countDown();
	        	
	        }});
	        Thread part3 = new Thread(new Runnable() {public void run() {
	     //   	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	        	Cruncher.destroyInRangeV2(morethings, centreD, worldObj, A, rMax, r0, rMax, rMax, 0, rMax, 0, rMax, vapourize);
	        	latch.countDown();
	        	
	        }});
	        Thread part4 = new Thread(new Runnable() {public void run() {
	     //   	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	        	Cruncher.destroyInRangeV2(morestuff, centreD, worldObj, A, rMax, r0, rMax, rMax, rMax, 0, 0, rMax, vapourize);
	        	latch.countDown();
	        	
	        }});
	        
	        part1.start();
	        part2.start();
	        part3.start();
	        part4.start();
	        //*/
	        

	        try {latch.await();} catch (InterruptedException e1) {}
	        long estimatedTime = System.nanoTime() - startTime;
	        System.out.println("Total time "+estimatedTime/1000000000+"s");
	        
	        int n = things.get(4)[0] + stuff.get(4)[0]+morethings.get(4)[0] + morestuff.get(4)[0];
	        
	   //     harm(worldObj, x0, y0, z0, r0);
	        
			safe.setDead();
	        System.gc();
	      }
	    });
	    
	    nextBoom.start();
	    
	  }
	  
	  public static void harm(World worldObj, double x, double y, double z, double r0){
		  
		  List<Entity> victims = new ArrayList<Entity>();
		  ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
		  
		  victims = safe.safeLocateEntity(worldObj, x, y, z, 80, 80, 80);
		  System.out.println(victims.size());
		  for(int i=0;i<victims.size();i++)
		  {
			  if(!victims.get(i).isDead&&vec.isVisibleEntityFromLocation(worldObj, victims.get(i), new double[] {x,y,z}))
			  {
				  double distSq = (x-victims.get(i).posX)*(x-victims.get(i).posX) + (y-victims.get(i).posY)*(y-victims.get(i).posY)+(z-victims.get(i).posZ)*(z-victims.get(i).posZ);
				  System.out.println("dealing "+(100*r0/distSq)+" to "+victims.get(i));
				  safe.safeHurt(victims.get(i), (int) (100*r0/distSq), DamageSource.generic);
			  }
		  }
	  }
	  
	  public void kineticImpactor(World worldObj, Vector3 velocity, Vector3 hitLocation, Vector3 acceleration, double mass, float energy)
	  {
		  if(mass<0||energy<=0)
		  {
			  return;
		  }
		  System.out.println("energy: "+energy+" Location: "+hitLocation.toString());
		  int n = 0;
		  List<Vector3> locations = new ArrayList<Vector3>();
		  List<Float> blasts = new ArrayList<Float>();
		  
		  float resist = hitLocation.getExplosionResistance(worldObj);
		  float blast = Math.min(resist, energy);
		  
		  if(resist>energy)
		  {
			  hitLocation = hitLocation.subtract(velocity.normalize());
			  Cruncher.destroyInRangeV3(hitLocation, worldObj, (int)(int) Math.max( (energy/2), 10), energy);
			  return;
		  }
		  
		  Vector3 absorbedLoc = new Vector3();
		  float remainingEnergy = 0;
		  int id = hitLocation.getBlockId(worldObj);
		  energy-=resist;
		  
		  while(energy>0)
		  {
			  locations.add(hitLocation.subtract(velocity.normalize()));
			  blasts.add(blast);
			  hitLocation = hitLocation.add(velocity.normalize());
			  id = hitLocation.getBlockId(worldObj);
			  velocity.add(acceleration);
			  resist = (float) Math.max(hitLocation.getExplosionResistance(worldObj),0);
			  blast = Math.min(resist, energy);
			  if(resist>energy)
			  {
				  absorbedLoc.set(hitLocation);
				  remainingEnergy = energy;
				  break;
			  }
			  energy-=(resist+0.1);
			  System.out.println(energy);
		  }
	//	  System.out.println("done energy: "+locations.size()+" "+blasts.get(0)+" "+locations.get(0).toString());
		  n = locations.size();
		  if(n!=0)
		  
		  for(int i = 0; i<n;i++)
		  {
			  Vector3 source = locations.get(i);
			  float strength = blasts.get(i)*blasts.get(i)*(((float)n-(float)i)/((float)n));
			  if(worldObj.doChunksNearChunkExist(source.intX(), source.intY(), source.intZ(), (int) (strength/2)))
			  {
				  if(i%2==0)
					  source.doExplosion(worldObj, strength, false);
				  System.out.println("blast "+i);
				  if(strength!=0)
				  Cruncher.destroyInRangeV3(source, worldObj, (int) Math.max(10, strength/2), strength);
			  }
			  else
			  {
				  System.out.println("not exist");
			  }
		  }
		  System.out.println("done blasts "+n);
		  
		  
		  if(remainingEnergy>10)
		  {
			  absorbedLoc = absorbedLoc.subtract(velocity.normalize());
			  Cruncher.destroyInRangeV3(absorbedLoc, worldObj, 50, remainingEnergy);
		  }
		  System.out.println("done remainder");
		  
	  }
	  
	  
	  public static class Cruncher
	  {
	    public Double[] set1 = new Double[]{123456d};
	    public Double[] set2 = new Double[]{123456d};
	    public Double[] set3 = new Double[]{123456d};
	    public Double[] set4 = new Double[]{123456d};
	    public Integer[] set5 = new Integer[]{123456};
	    public Integer[] set6 = new Integer[]{123456};
	    public Integer[] set7 = new Integer[]{123456};
	    public Integer[] set8 = new Integer[]{123456};
	    public Double[] set9 = new Double[]{123456d};
	    public Double[] set10 = new Double[]{123456d};
	    public short[][] set11 = new short[][] {{12345}};
	    public int n;
	    public Boolean[] done = { Boolean.valueOf(false) };

	    double temp = 0.0D;
	    int temp2 = 0;
	    float temp3 = 0.0F;
	    short[] temp4 = {0};

	    public void sort2(Double[] vals1, short[][] vals2){
	    	if ((vals1 == null) || (vals1.length == 0)) {
		        return;
		      }
		      this.set1 = vals1;
		      this.set11 = vals2;
		      this.n = this.set1.length;
	    }
	    
	    public void sort3(Double[] vals1, Double[] vals2, Double[] vals3)
	    {
	      if ((vals1 == null) || (vals1.length == 0)) {
	        return;
	      }
	      this.set1 = vals1;
	      this.set2 = vals2;
	      this.set3 = vals3;
	      this.n = this.set1.length;

	      quicksort(0, this.n - 1);
	    }

	    public void sort5(Double[] vals1, Integer[] vals2, Integer[] vals3, Integer[] vals4, Integer[] vals5)
	    {
	      if ((vals1 == null) || (vals1.length == 0)) {
	        return;
	      }
	      this.set1 = vals1;
	      this.set5 = vals2;
	      this.set6 = vals3;
	      this.set7 = vals4;
	      this.set8 = vals5;
	      this.n = this.set1.length;

	      quicksort(0, this.n - 1);
	    }

	    public void sort6(Double[] vals1, Integer[] vals2, Integer[] vals3, Integer[] vals4, Double[] vals5, Double[] vals6)
	    {
	      if ((vals1 == null) || (vals1.length == 0)) {
	        return;
	      }
	      this.set1 = vals1;
	      this.set5 = vals2;
	      this.set6 = vals3;
	      this.set7 = vals4;
	      this.set2 = vals5;
	      this.set3 = vals6;
	      this.n = this.set1.length;

	      quicksort(0, this.n - 1);
	    }

	    public void sort7(Double[] vals1, Integer[] vals2, Integer[] vals3, Integer[] vals4, Double[] vals5, Double[] vals6, Integer[] vals7)
	    {
	      if ((vals1 == null) || (vals1.length == 0)) {
	        return;
	      }
	      this.set1 = vals1;
	      this.set5 = vals2;
	      this.set6 = vals3;
	      this.set7 = vals4;
	      this.set2 = vals5;
	      this.set3 = vals6;
	      this.set8 = vals7;
	      this.n = this.set1.length;

	      quicksort(0, this.n - 1);
	    }

	    private void quicksort(int low, int high)
	    {
	      int i = low; int j = high;
	      double pivot = this.set1[(low + (high - low) / 2)].doubleValue();
	      while (i <= j) {
	        while (this.set1[i].doubleValue() < pivot) i++;
	        while (this.set1[j].doubleValue() > pivot) j--;
	        if (i <= j) {
	          exchange(i, j);
	          i++;
	          j--;
	        }
	      }
	      if (low < j)
	        quicksort(low, j);
	      if (i < high)
	        quicksort(i, high);
	    }

	    private void exchange(int i, int j)
	    {
	    	if((this.set1[0]!=123456d) || (this.set1.length == this.n)) {
		      this.temp = this.set1[i].doubleValue();
		      this.set1[i] = this.set1[j];
		      this.set1[j] = Double.valueOf(this.temp);
	    }
	      if ((this.set2[0]!=123456d)  || (this.set2.length == this.n)) {
	        this.temp = this.set2[i].doubleValue();
	        this.set2[i] = this.set2[j];
	        this.set2[j] = Double.valueOf(this.temp);
	      }
	      if ((this.set3[0]!=123456d)  || (this.set3.length == this.n)) {
	        this.temp = this.set3[i].doubleValue();
	        this.set3[i] = this.set3[j];
	        this.set3[j] = Double.valueOf(this.temp);
	      }
	      if ((this.set4[0]!=123456d)  || (this.set4.length == this.n)) {
	        this.temp = this.set4[i].doubleValue();
	        this.set4[i] = this.set4[j];
	        this.set4[j] = Double.valueOf(this.temp);
	      }
	      if ((this.set5[0]!=123456) || (this.set5.length == this.n)) {
	        this.temp2 = this.set5[i].intValue();
	        this.set5[i] = this.set5[j];
	        this.set5[j] = (this.temp2);
	      }
	      if ((this.set6[0]!=123456) || (this.set6.length == this.n)) {
	        this.temp2 = this.set6[i].intValue();
	        this.set6[i] = this.set6[j];
	        this.set6[j] = (this.temp2);
	      }
	      if ((this.set7[0]!=123456)|| (this.set7.length == this.n)) {
	        this.temp2 = this.set7[i].intValue();
	        this.set7[i] = this.set7[j];
	        this.set7[j] = (this.temp2);
	      }
	      if ((this.set8[0]!=123456) || (this.set8.length == this.n)) {
	        this.temp2 = this.set8[i].intValue();
	        this.set8[i] = this.set8[j];
	        this.set8[j] = (this.temp2);
	      }
	      if ((this.set9[0]!=123456d) || (this.set9.length == this.n)) {
	        this.temp = this.set9[i].doubleValue();
	        this.set9[i] = this.set9[j];
	        this.set9[j] = Double.valueOf(this.temp);
	      }
	      if ((this.set10[0]!=123456d) || (this.set10.length == this.n)) {
		        this.temp = this.set10[i].doubleValue();
		        this.set10[i] = this.set10[j];
		        this.set10[j] = Double.valueOf(this.temp);
		  }
	      if((this.set11.length == this.n)){
	    	  this.temp4 = this.set11[i];
		        this.set11[i] = this.set11[j];
		        this.set11[j] = this.temp4;
	      }
	    }
	    
	    
	    public static short[][] sortValues(short zMax,short zMin,short yMax,short yMin,short xMax,short xMin){
	    	short[][] quadrant;
	    	Double[] radii;
	    	List<short[]> templist = new ArrayList<short[]>();
	    	List<Double> tempRadii = new ArrayList<Double>();
	    	Cruncher sort = new Cruncher();
	    	
	    	for (short z = (short) -zMin; z < zMax; z++)
		        for (short y = (short) -yMin; y < yMax; y++)
		          for (short x = (short) -xMin; x < xMax; x++) {
		        	  templist.add(new short[] {x,y,z});
		        	  tempRadii.add((double)(x*x+y*y+z*z));
		          }
	    	quadrant = templist.toArray(new short[0][0]);
	    	radii = tempRadii.toArray(new Double[0]);
	    	templist.clear();
	    	tempRadii.clear();
	    	
	    	
	    	sort.sort2(radii, quadrant);
	    	
	    	radii = sort.set1;
	    	quadrant = sort.set11;
	    	
	    	sort.set1 = null;
	    	sort.set11 = null;
	    	return quadrant;
	    }
	    

	    public static List<Integer[]> destroyInRangeV2(List<Integer[]> list, Double[] centre,World worldObj,double scaleFactor,int rMax,double r0,int zMax,int zMin,int yMax,int yMin,int xMax,int xMin,boolean vapourize)
	    {
	      List<Integer> blastR = new ArrayList<Integer>();
	      List<Integer> x0PosR = new ArrayList<Integer>();
	      List<Integer> y0PosR = new ArrayList<Integer>();
	      List<Integer> z0PosR = new ArrayList<Integer>();
	    //  System.out.println("der");
	      BitSet blockedLocations = new BitSet();
	      
	      IntMap map = new IntMap();
	      IntMap resists = new IntMap();
	      
	      //Map<Integer, Integer> resists = new HashMap<Integer, Integer>();
	      
	      double x0 = centre[0];
	      double y0 = centre[1];
	      double z0 = centre[2];

	      ThreadSafeWorldOperations world = new ThreadSafeWorldOperations();
	      
	      double vectMag, vHatx, vHaty, vHatz, reflect;
	      int id=0,idTest=0, xtest, ytest, ztest, xtestprev, ytestprev,ztestprev,index,x,y,z,z1,vectMagSq,
	    		  xVal = xMin+1,
	    		  yVal = (yMin+yMax+1),
	    		  zVal = 1+(zMax+zMin)*(zMax+zMin);
	      Float prevDamp = 0.0F, damp = 0.0F, dj, blastResist,j,resist;
	      
	      int n = 0, meta, metaTest, m = 0, l = 0, o = 0;
	      
	      boolean inRange;
	      for (z = 0; z < zMax; z++) {
	        for (y = -yMin; y < yMax; y++) {
	          for (x = -xMin; x < xMax; x++) {
	            for(int k = -1;k<2;k+=2){
	            	z1=k*z;

		        	resist=0f;
		        	damp = 0f;
		        	prevDamp = 0.0f;
		            vectMagSq = (z1 * z1 + x * x + y * y);
		            vectMag = Math.sqrt(z1 * z1 + x * x + y * y);
		            vHatx = x / vectMag;
		            vHaty = y / vectMag;
		            vHatz = z1 / vectMag;
		            
	            if (vectMagSq < rMax*rMax) {
	            	//*
	              if(world.safeLookUp(worldObj,x + x0, z1 + z0, y + y0)){
	              
	              
	              id = world.ID;
	              meta = world.meta;
	              index =  x + xVal + (y + yMin) * (yVal) +  (z1 + zMin) * zVal;
	              
	            blastResist = world.blastResistance;
	              
            if(world.ID==Block.grass.blockID) blastResist/=2;
            if(world.ID==Block.glass.blockID||world.ID==Block.thinGlass.blockID) blastResist = 0f;
            
            if(blastResist<=1) blastResist/=10;
            
            if (vapourize&&world.isLiquid(worldObj,(int)(x + x0), (int)(z1 + z0), (int)(y + y0))&&(vectMag < r0/5)){ 
            	blastResist = 0.0F;
            }
            
	              inRange = true;
	              if ((id == 0||id==DUSTID) || (blastResist > r0 * scaleFactor / (vectMagSq))) { 
	            	  
	            	  inRange = false;
	            	  if (map.contains(index) &&( map.get(index) <= vectMagSq)) {  
		                    inRange = false;
		                    m++;
		                    reflect = r0 * scaleFactor / (vectMagSq);
		                    
		                    if(!blockedLocations.get(index)&&reflect>30){
			                    x0PosR.add((x));
			                    y0PosR.add((y));
			                    z0PosR.add((z1));
			                    blastR.add(((int)(1000.0D * reflect)));
			                    blockedLocations.set(index, true);
		                    }
		                    
		                  }
	            	  
	              } else {
	            	  
	            	  xtestprev = 0; ytestprev = 0; ztestprev=0;
	            	  
	            	  dj = (float)(1);
	            	  for (j = 0F; j < vectMag; j+=dj) {
	                  xtest = (int)(j * vHatx);
	                  ytest = (int)(j * vHaty);
	                  ztest = (int)(j * vHatz);
	                  
	                  if(!(xtest==xtestprev&&ytest==ytestprev&&ztest==ztestprev)){
	                  
	                 index =  xtest + xVal + (ytest + yMin) * (yVal) +  (ztest + zMin) * zVal;
	              
	                  if (map.contains(index) &&( map.get(index) <= vectMagSq)) {  
	                    inRange = false;
	                    m++;
	                    reflect = r0 * scaleFactor / (vectMagSq);
	                    
	                    if(!blockedLocations.get(index)&&reflect>30){
		                    x0PosR.add((xtest));
		                    y0PosR.add((ytest));
		                    z0PosR.add((ztest));
		                    blastR.add(((int)(1000.0D * reflect)));
		                    blockedLocations.set(index, true);
	                    }
	                    
	                    break;
	                  }
	                  
	                  if(world.safeLookUp(worldObj,xtest + x0, ztest + z0, ytest + y0)){
	                  idTest = world.ID;
	                  metaTest = world.meta;
	                  if (!(idTest == 0||idTest==DUSTID)) 
	                  {
	                	  { 
		                    resist = world.blastResistance;
		                    if(world.ID==Block.grass.blockID) resist/=2;
		                    if(resist<=1) resist/=10;
		                    if (vapourize&&world.isLiquid(worldObj,(int)(xtest + x0), (int)(ztest + z0), (int)(ytest + y0))&&(vectMag < r0/5)){ 
		                    	resist = 0.0F; 
		                    	world.safeSet(worldObj,xtest + x0, ztest + z0, ytest + y0, 0, 0);
		                    }
	                	 }
	                    
	                    damp = resist;
	                    
	                    if (damp >= (r0 * scaleFactor / (vectMagSq))) {
	                      inRange = false;
	                      
	                      map.put(index, vectMagSq);
	                      reflect = r0 * scaleFactor / (vectMagSq);
	                      if(!blockedLocations.get(index)&&reflect>30){
		                      blockedLocations.set(index, true);
		                      x0PosR.add((xtest));
		                      y0PosR.add((ytest));
		                      z0PosR.add((ztest));
		                      blastR.add(((int)(1000.0D * reflect)));
	                      }
	                      break;
	                    }else{
	                      
		                  world.safeSet(worldObj,xtest+x0, ztest+z0, ytest+y0, 0, 0);
	                      resists.put(index, (int) (resist*100));
		                  l++;
		                  if(resist>1){
		                	  n++;
		                  }
		                  
	                    }
	                  }
	            	  }}
	                 prevDamp = damp;
	                  xtestprev = xtest; ytestprev=ytest;ztestprev=ztest;
	                }
	              }
	              //*
	              if(inRange){
	            	  world.safeSet(worldObj,x+x0, z1+z0, y+y0, 0, 0);
	            	  index =  x + xVal + (y + yMin) * (yVal) +  (z1 + zMin) * zVal;
                      resists.put(index, (int) (resist*100));
	            	  l++;
	            	  if(blastResist>1){
	                	  n++;
	                  }
	              }
	              //*/
	              
	            }}}
	            
	            
	          }
	        }
	      }
	  //    System.out.println(m+" "+l+" "+o);
	      blockedLocations.clear();
	      world.setDead();
	      list.add(blastR.toArray(new Integer[0]));
	      blastR.clear();
	      list.add(x0PosR.toArray(new Integer[0]));
	      x0PosR.clear();
	      list.add(y0PosR.toArray(new Integer[0]));
	      y0PosR.clear();
	      list.add(z0PosR.toArray(new Integer[0]));
	      z0PosR.clear();
	      list.add(new Integer[] {n});
	      return list;
	    }
	    
	    public static void destroyInRangeV3(Vector3 centre,World worldObj,int radius,double strength)
	    {
	      //Map<Integer, Integer> resists = new HashMap<Integer, Integer>();
	      double scaleFactor = 10;
	      double x0 = centre.x;
	      double y0 = centre.y;
	      double z0 = centre.z;
	      double radSq = radius*radius;
	      int n = 0;
	      int l = 0;
	      
	      Map<Vector3, Integer[]> removed = new HashMap<Vector3, Integer[]>();
	      
	      for(int z = -radius; z<=radius; z++)
	    	  for(int y = -radius; y<=radius; y++)
	    		  for(int x = -radius; x<=radius; x++)
	    		  {
	    			  double rSq = x*x+y*y+z*z;
	    			  if(rSq>radSq)
	    				  continue;
	    			  Vector3 r = new Vector3(x,y,z);
	    			  if(r.add(centre).getBlockId(worldObj)==0&&!(x==0&&y==0&&z==0))
	    				  continue;
	    			  float resist = r.add(centre).getExplosionResistance(worldObj);
	    			  if(resist>strength*scaleFactor/r.magSq())
		    			  continue;
	    			  
	    			  Vector3 rHat = r.normalize();
	    			  Vector3 rTest = centre;
	    			  Vector3 rTestPrev = new Vector3();
	    			  double rMag = r.mag();
	    			  float damp = 0;
	    			  boolean valid = true;
	    			  
	    			  for(double i = 0; i<=rMag; i++)
	    			  {
	    				  rTest = centre.add(rHat.scalarMult(i));
	    				  if(!rTest.sameBlock(rTestPrev))
	    				  {
	    					  n++;
	    					  resist = rTest.getExplosionResistance(worldObj);

	    					  damp += resist;
	    						  
	    					  
	    					  if(damp>strength*scaleFactor/(rHat.scalarMult(i).magSq()))
	    					  {
	    						  valid = false;
	    						  l++;
	    						  break;
	    					  }
	    				  }
	    				  rTestPrev = rTest.Copy();
	    			  }
	    			  if(valid&&r.getBlockId(worldObj)!=DUSTID&&r.getBlockId(worldObj)!=BlockLava.getInstance(3).blockID)
	    			  {
	    				  if(!(x==0&&y==0&&z==0))
	    					  removed.put(r.add(centre),new Integer[]{DUSTID,15});
	    				  else
	    					  removed.put(r.add(centre),new Integer[]{BlockLava.getInstance(3).blockID,0});
	    				  
	    			  }
	    		  }
	      System.out.println(removed.size()+" "+strength+" "+n+" "+l);
	      for(Vector3 r: removed.keySet())
	      {
	    	  r.setBlock(worldObj, removed.get(r)[1], removed.get(r)[0]);
	      }
	      removed.clear();
	      
	    }
	
	
	}
}
