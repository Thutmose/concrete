package thutconcrete.common.tileentity;

import icbm.api.explosion.ExplosionEvent;
import icbm.api.explosion.IExplosive;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.io.ByteArrayDataInput;

import thutconcrete.api.utils.Vector3;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.Volcano;
import thutconcrete.common.blocks.Block16Fluid;
import thutconcrete.common.blocks.BlockMisc;
import thutconcrete.common.blocks.BlockDust;
import thutconcrete.common.blocks.BlockLava;
import thutconcrete.common.blocks.BlockSolidLava;
import thutconcrete.common.blocks.BlockVolcano;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.network.PacketInt;
import thutconcrete.common.network.PacketVolcano;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.ExplosionCustom.Cruncher;
import thutconcrete.common.utils.LinearAlgebra;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityVolcano extends TileEntity
{
	
	public int typeid = 10;
	public int height = 0;
	//public int z;
	public int ventCount = 0;
    int n=0;
   
    public Volcano v;
    
    public static List<Integer> replaceable =new ArrayList<Integer>();
    public static List<Integer> lava = new ArrayList<Integer>();
    public static List<Integer> solidlava = new ArrayList<Integer>();
    public boolean firstTime = true;
    public boolean erupted = false;
    public boolean active = false;
    public static int ashAmount = ConfigHandler.ashAmount;;

    public static double eruptionStartRate;
    public static double eruptionStopRate;
    
    public int z;
    
    public static int minorExplosionRate;
    public static int majorExplosionRate;
    
    public static double dormancyRate;
    public static double activityRate;
    
    public long age = 0;
    
    public String[] types = 
    	{
    		"Mafic",
    		"Intermediate",
    		"Felsic",
    	};
    
    public int[][] sides = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1},{0,0}};
    public int[][] extendedSides =  {{0,2},{0,-2},{2,0},{-2,0},{2,2},{2,-2},{-2,2},{-2,-2}};
    
    public List<Vect> sideVents = new ArrayList<Vect>();
    public Vect mainVent = new Vect(0,1,0);
    
    //private static ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
    
	public List<PlumeParticle> particles = new ArrayList<PlumeParticle>();
	public List<PlumeParticle> deadParticles = new ArrayList<PlumeParticle>();
	
	public boolean doop = false;
	
	private double x0,y0,z0;
	private boolean read = false;
	public double r0 = 0;
	public int seed = new Random().nextInt(1000);
	private double[] wind = {0,0};
	private int num = 7;
	private int dustId = BlockDust.instance.blockID;
	double rMax;
	public boolean first = true;
	public boolean dormant = false;
	public int time = 0;
	Random rand = new Random();
	int index = 0;
	public int growthTimes = 0;
	public static int tickRate;
	public int activeCount = 0;
	
	@Override
	public void updateEntity()
	{
		if(tickRate<=0)
		{
			tickRate = 1;
		}
		//System.out.println(age%tickRate);
		if(age%(tickRate*(typeid+1))==0&&ConfigHandler.volcanosActive)
		{
		//	System.out.println(age);
			if(firstTime)
			{
				init();
			}
			if(!dormant)
			{
				volcanoTick();
				plumeTick();
				
				if(growthTimes<=100)
				{
					double factor = 1-((double)10 - (double)activeCount)/(double)10;
					v.growthFactor = 1+(factor);
					if(ConfigHandler.debugPrints)
					System.out.println("growth Factor: "+v.growthFactor+" "+worldObj+" "+activeCount+" "+growthTimes+" ");
				}
				if(doop||(activeCount>10&&growthTimes<=100))
				{
					if(ConfigHandler.debugPrints)
					System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+z+" growth Event "+growthTimes+" "+worldObj);
					growthTimes++;
					activeCount++;
					
					ExplosionEvent evt = new ExplosionEvent(worldObj, xCoord, yCoord+mainVent.y+mainVent.var1, z, getExplosive(0, typeid));
					MinecraftForge.EVENT_BUS.post(evt);
					activeCount = 0;
					v.growthFactor = 1;
					if(!worldObj.isRemote)
						rapidGrowthTick();
				}
				if(growthTimes>=100&&growthTimes<=102)
				{
					v.growthFactor = 1;
					growthTimes++;
				}
				
				maintainMagma();
				if(rand.nextGaussian()>dormancyRate)
				{
					dormant = true;
					v.activeFactor = 0.01;
					if(ConfigHandler.debugPrints)
					System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+z+" changed to activity state: "+getState()+" "+worldObj);
				}
			}
			else if(rand.nextGaussian()>activityRate)
			{
				dormant = false;
				v.activeFactor = 1;
				if(ConfigHandler.debugPrints)
				System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+z+" changed to activity state: "+getState()+" "+worldObj);
			}
		}
		age++;
	}
	
	public static IExplosive getExplosive(final int type, final int type1)
	{
		return new IExplosive() {
			
			int factor = type+type1;
			
			@Override
			public String getUnlocalizedName() {
				return "growthEvent";
			}
			
			@Override
			public int getTier() {
				return 0;
			}
			
			@Override
			public float getRadius() {
				return 16;
			}
			
			@Override
			public String getMissileName() {
				return null;
			}
			
			@Override
			public String getMinecartName() {
				return null;
			}
			
			@Override
			public int getID() {
				return 0;
			}
			
			@Override
			public String getGrenadeName() {
				return null;
			}
			
			@Override
			public String getExplosiveName() {
				return "Volcanic Growth Event";
			}
			
			@Override
			public double getEnergy() {
				return 5*Math.pow(10, 13+(factor*factor))/(type+1);
			}
		};
	}
	
	public void setDormancy(boolean bool)
	{
		dormant = bool;
		System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+z+" changed to activity state: "+getState()+" "+worldObj);
	}
	
    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
    	typeid = 10;
    	Volcano.removeVolcano(xCoord, z);
        this.tileEntityInvalid = true;
    }

	
	private void init()
	{
		ashAmount = ConfigHandler.ashAmount;
		firstTime = false;
		v = Volcano.getVolcano(xCoord, z, worldObj);
		if(ConfigHandler.debugPrints)
			System.out.println("frequency: "+v.frequency+" "+worldObj+" "+Volcano.seedFromBlock(xCoord, z, worldObj));
		if(typeid>2)
		{
			height = v.h;
			typeid = v.type;
			if(ConfigHandler.debugPrints)
			System.out.println(types[typeid]+" "+worldObj);
			r0 = height/2;
			n = ashAmount*(typeid*typeid+1);
			ventCount = (int) (10*Math.random());
			mainVent.i = height+64-yCoord;
			mainVent.r = 2*rand.nextInt(majorExplosionRate);
			mainVent.k = 40*(typeid+1);
			mainVent.j = 0;
			mainVent.bool = false;
			mainVent.var1 = 0;
			for(int i = 0; i<ventCount; i++)
			{
				sideVents.add(new Vect(new double[] {2*(Math.random()-0.5), Math.random(), 2*(Math.random()-0.5)},
				height, mainVent.i*Math.random(), Math.random()*10*(typeid+1), 0.85*mainVent.r , false));
			}
			
		}
		
		if(replaceable.size()==0){
			replaceable.add(0);
			replaceable.add(Block.stone.blockID);
			replaceable.add(Block.gravel.blockID);
			replaceable.add(Block.grass.blockID);
			replaceable.add(Block.waterMoving.blockID);
			replaceable.add(Block.waterStill.blockID);
			replaceable.add(Block.lavaMoving.blockID);
			replaceable.add(Block.lavaStill.blockID);
			replaceable.add(BlockDust.instance.blockID);
			
			if(!lava.contains(BlockLava.getInstance(0).blockID))
			{
	
				for(Block block:Block.blocksList){
					if(block!=null){
					String name = block.getUnlocalizedName().toLowerCase();
					if(name.contains("ore")
							||name.contains("dirt")	
							||name.contains("sand")	
							||name.contains("stone")		
							||name.contains("chalk")		
							||name.contains("rock")	
							||block.getExplosionResistance(null)<100
							)
					{
						replaceable.add(block.blockID);
					}
					}
				}
				for(int i=0;i<3;i++){
					solidlava.add(BlockSolidLava.getInstance(i).blockID);
					lava.add(BlockLava.getInstance(i).blockID);
				}
	
			}
		
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, z);
	}
	
	private void setLava(double x, double y, double z)
	{
		if(!worldObj.isRemote)
		{
			for(int[] side : sides)
			{
				int id = worldObj.getBlockId((int)x+side[0], (int)y, (int)z+side[1]);
				
				if((replaceable.contains(id)||solidlava.contains(id)))
				{
					worldObj.setBlock((int)x+side[0], (int)y, (int)z+side[1], BlockLava.getInstance(typeid).blockID, 0,3);
				}
				else if(lava.contains(id))
				{
					int meta = worldObj.getBlockMetadata((int)x+side[0], (int)y, (int)z+side[1]);
					if(meta!=0)
					{
						worldObj.setBlock((int)x+side[0], (int)y, (int)z+side[1], BlockLava.getInstance(typeid).blockID, 0,3);
					}
				}
			}
		}
	}
	
   public void writeToNBT(NBTTagCompound par1)
   {
	   super.writeToNBT(par1);
	   par1.setInteger("type", typeid);
	   par1.setInteger("h", height);
	   par1.setInteger("z location", z);
	   par1.setInteger("veinCount", ventCount);
	   par1.setInteger("growth", growthTimes);
	   for(int i = 0; i<ventCount; i++)
	   {
		   Vect vec = sideVents.get(i);
		   vec.writeToNBT(par1, Integer.toString(i));
	   }
	   mainVent.writeToNBT(par1, "main");
	   par1.setBoolean("active", active);
	   par1.setBoolean("erupted", erupted);
	   par1.setBoolean("dormant", dormant);
	   par1.setLong("age", age);
	   par1.setInteger("activeAge", activeCount);
   }

   public void readFromNBT(NBTTagCompound par1)
   {
      super.readFromNBT(par1);
      typeid = par1.getInteger("type");
      height = par1.getInteger("h");
      growthTimes = par1.getInteger("growth");
      z = par1.getInteger("z location");
      ventCount = par1.getInteger("veinCount");
	   for(int i = 0; i<ventCount; i++)
	   {
		   sideVents.add(Vect.readFromNBT(par1,Integer.toString(i)));
	   }
	   mainVent = Vect.readFromNBT(par1, "main");
	   mainVent.var1 = 0;
	   active = par1.getBoolean("active");
	   erupted = par1.getBoolean("erupted");
	   dormant = par1.getBoolean("dormant");
	   age = par1.getLong("age");
	   activeCount = par1.getInteger("activeAge");
	   if(ConfigHandler.debugPrints)
	   System.out.println("Loaded "+types[typeid]+" Volcano at location "+xCoord+" "+z+" of activity state: "+getState()+". Maximum Height of:  "+(height+64)+". Number of Vents: "+(sideVents.size()+1));
   }

   public int getZCoord()
   {
	   return z;
   }
   
   public String getState()
   {
	   String dormancy = dormant?"dormant":"active";
	   String activity = active?"erupting":"not erupting";
	   String state = dormant?dormancy:dormancy+" "+activity;
	   return state;
   }
   
   private void volcanoTick()
	   {
		   if(active&&rand.nextGaussian()>0)
			{
			  if(ventCount>0&&Math.random()>0.75)
			  {
				growVent(sideVents.get(index));
				index = (index+1)%sideVents.size();
			  }
			  growVent(mainVent);
			  if(rand.nextGaussian()>eruptionStopRate)
			  {
				active = false;
				v.activeFactor = 0.5;
				if(ConfigHandler.debugPrints)
				System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+z+" changed to activity state: "+getState());
			  }
			  activeCount++;
			}
			
		  if (!active&rand.nextGaussian()>eruptionStartRate)
		   {
			   active = true;
				v.activeFactor = 1;
			   if(ConfigHandler.debugPrints)
				System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+z+" changed to activity state: "+getState());
		   }
	   }
   
   private void rapidGrowthTick()
   {
	   double rad = 16;
	   	x0 = xCoord+mainVent.x; y0 =  yCoord+mainVent.y+mainVent.var1; z0 = getZCoord()+mainVent.z;
	   	r0 = rad;
	   	num = 0;
	   	n = (int)(0.05*ashAmount);
		worldObj.createExplosion(null, x0, y0, z0, (float) rad, false);
	   	addPlumeParticles(0.5, BlockLava.getInstance(typeid).blockID);
	   	erupted = true;
	   	doop = false;
   }
	      
   private void plumeTick()
	   {
		   if(ashAmount>1&&particles.size()>0&&!worldObj.isRemote)
		   {
				Vector3 vec = new Vector3(xCoord+0.5, yCoord+1.5, z+0.5);
				vec = (vec.getNextSurfacePoint(worldObj, vec, new Vector3(0,1,0), 255-yCoord));
			   if((vec==null))
				   plumeCalculations();
		   }
	   }

	private void plumeCalculations()
		{
		
		if(particles.size()==0)
		{
			erupted = false;
			if(!active)
			{
				active = true;
				if(ConfigHandler.debugPrints)
				System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+z+" changed to activity state: "+getState());
			}
			return;
		}
		
		for(PlumeParticle p: particles)
		{
			int y = (int)p.y;
			int id = worldObj.getBlockId((int)p.x, y, (int)p.z);
			int idP = (int)p.vy;
			
	        boolean canBreak = Block16Fluid.instance.willBreak(id);

	        boolean breakException = Block16Fluid.instance.breakException(idP, id);
	        
			if(id==0||(canBreak&&!breakException)&&worldObj.doChunksNearChunkExist((int)p.x, y, (int)p.z, 5))
			{
				worldObj.setBlock((int)p.x, y, (int)p.z, idP, (int)p.vx, 3);
		//		worldObj.scheduleBlockUpdate((int)p.x, y, (int)p.z, idP, 10);
			}
		}
		
		particles.clear();
		
	}
	
	private void addPlumeParticles(double Hfactor, int id)
	{
		int typeFactor = typeid == 2? (int)(Hfactor*50+200): typeid==1? (int)(Hfactor*50+150): (int)(Hfactor*74+136);
		Random r = new Random();
		r0 = Math.min(r0,50); //Limits the size to "50"
		while(n>0)
		{
			double x,z, dx=0, dz=0, h;
		
			x = r0*r.nextGaussian();
			z = r0*r.nextGaussian();	
			
			
			if(id==BlockDust.instance.blockID)
			{
				dx = ThreadSafeWorldOperations.getWind(worldObj, x, z)[0];
	    		dz = ThreadSafeWorldOperations.getWind(worldObj, x, z)[1];
			}

			int y=typeFactor-r.nextInt(50);
			h = y-1;	

	    	int id1 = worldObj.getBlockId((int)x+(int)(dx*(y-h)), (int)h, (int)z+(int)(dz*(y-h)));
			
	    	boolean fell = false;

		    /*	
	    	while(h>1)
	    	{
	    		id1 = worldObj.getBlockId((int)x+(int)(dx*(1+y-h)), (int)h-1, (int)z+(int)(dz*(1+y-h)));
	    		if(!(id1==0||Block16Fluid.instance.breaks.contains(id1)||(id==BlockLava.getInstance(typeid).blockID&&(id1==Block.waterMoving.blockID||id1==Block.waterStill.blockID))))
	    		{
	    			break;
	    		}
	    		h--;
	    	}
	   //*/ 	
	    	id1 = worldObj.getBlockId((int)x+(int)(dx*(y-h)), (int)h, (int)z+(int)(dz*(y-h)));
	    	if(!(id1==0||Block16Fluid.instance.breaks.contains(id1)||(id==BlockLava.getInstance(typeid).blockID&&(id1==Block.waterMoving.blockID||id1==Block.waterStill.blockID))))
	    	{
	    		n--;
	    	}
	    	else
	    	{
			PlumeParticle particle = new PlumeParticle();
			
			particle.x = x+x0;
			particle.y = h;
			particle.z = z+z0;
			
			particle.vx = num;
			particle.vy = id;
			particles.add(particle);
			n--;
	    	}
		}
		
	//	System.out.println(particles.size());
	}
	
	void growVent(Vect vent)
	{
		double maxLength = vent.i;
		
		double x = vent.x,y = vent.y,z = vent.z, h = vent.j, e = vent.k, r = vent.r;
		int countMinor = vent.var2, countMajor = vent.var3;
		int majorRate = (int) (vent==mainVent?majorExplosionRate:0.85*majorExplosionRate);
		if(vent!=mainVent && h>mainVent.var1)
		{
			return;
		}

		int i = 1;
		int id = 1;

		boolean toErupt = false;
		
		while(id!=0&&i*y+h<=maxLength)
		{
			i++;
			id = worldObj.getBlockId( (int)(xCoord+i*x),(int) (yCoord+i*y+h),(int) (getZCoord()+i*z));
			
			if(!(lava.contains(id)||replaceable.contains(id)||solidlava.contains(id))) break;
			
			setLava(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z);
			if(vent == mainVent)
			{
				mainVent.var1 = (int) (i*y+h);
			}
			if(yCoord+i*y+h>64&&rand.nextGaussian()>r&&!erupted)
			{
				toErupt = true;
			}
		}
		
		if(mainVent.var1 > 0.75*maxLength&&countMajor>majorExplosionRate)
		{
			active = false;
			if(ConfigHandler.debugPrints)
			System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+this.z+" changed to activity state: "+getState()+" "+worldObj);
		}
		

	  if(age>2500&&countMinor>minorExplosionRate)
	  {
		  Vector3 centre = new Vector3(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z);
		  //*/
	  
		  if(!worldObj.isRemote)
		  {
			  if(ConfigHandler.debugPrints)
				  System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+this.z+" minor Explosion "+centre.toString()+" "+worldObj);
			
			  double rad = Math.random()*100;
				ExplosionCustom boom = new ExplosionCustom();
				
		    	boom.doExplosion2(centre, worldObj, (int) (rad), rad, false, false);
		    	worldObj.playSoundEffect(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, "random.explode", 10.0F, 1.0F);
		    	
		    	x0 = xCoord+i*x; y0 =  yCoord+i*y+h; z0 = getZCoord()+i*z;
		    	r0 = rad/5;
		    	num = 0;
		    	n = (int)(0.01*ashAmount*Math.random());
		    	addPlumeParticles(0.5, BlockLava.getInstance(typeid).blockID);
		    	//*/
		    	
				ExplosionEvent evt = new ExplosionEvent(worldObj, x0, y0, z0, getExplosive(1, typeid));
				MinecraftForge.EVENT_BUS.post(evt);
		  }
			
	    	vent.var2 = 0;
	    	erupted = true;
	  }
		
		if(age>2500&&toErupt)
		{
			//*/
			
			
			if(!worldObj.isRemote)
			{
				Vector3 centre = new Vector3(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z);
				if(ConfigHandler.debugPrints)
				  System.out.println(types[typeid]+" Volcano at location "+xCoord+" "+this.z+" major Explosion "+centre.toString()+" "+worldObj);
				ExplosionCustom boom = new ExplosionCustom();
				double rad = Math.random()*e;
				boom.doExplosion(worldObj,xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, rad, false);
		    	worldObj.playSoundEffect(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, "random.explode", 10.0F, 1.0F);
		    	x0 = xCoord+i*x; y0 =  yCoord+i*y+h; z0 = getZCoord()+i*z;
		    	r0 = e;
		    	num = 7;
		    	n = (int) (vent==mainVent?ashAmount*(typeid*typeid+1):0.1*ashAmount*(typeid*typeid+1));
		    	addPlumeParticles(vent==mainVent?1:0.5, BlockDust.instance.blockID);
		    	//*/
		    	doop = false;
				ExplosionEvent evt = new ExplosionEvent(worldObj, x0, y0, z0, getExplosive(2, typeid));
				MinecraftForge.EVENT_BUS.post(evt);
			}
		    erupted = true;
		    vent.var3 = 0;
		}
		vent.var2++;
		vent.var3++;
		
		double majorDiff = 1-((double)majorExplosionRate - (double)vent.var3)/(double)majorExplosionRate;
		double minorDiff = 1-((double)minorExplosionRate - (double)vent.var2)/(double)minorExplosionRate;
		
		//System.out.println(majorDiff+" "+minorDiff+" "+(majorDiff*majorDiff)+" "+(minorDiff*minorDiff));
		
		v.majorFactor = 1+(majorDiff*majorDiff);
		v.minorFactor = 1+(minorDiff*minorDiff);
		

		if(ConfigHandler.debugPrints)
		System.out.println("major Factor: "+v.majorFactor+", Minor Factor: "+v.minorFactor+" "+worldObj );
	}
	
	void maintainMagma()
	{
		
		setLava(xCoord, yCoord+1, z);
		if(yCoord>=40) return;
		
		for(int h = yCoord+1; h<40; h++)
		{
			int id = worldObj.getBlockId(xCoord, h, z);
			if(!(lava.contains(id)||replaceable.contains(id)||solidlava.contains(id))) break;
			
			setLava(xCoord,h, getZCoord());
		}
	}
	
    @Override
    public Packet getDescriptionPacket()
    {
        return PacketVolcano.getPacket(this);
    }
	
	public static class PlumeParticle
	{
		public double x,y,z,vx,vy,vz,dvy;
		
		public static PlumeParticle readFromNBT(NBTTagCompound cmpnd, String tag)
		{
			PlumeParticle tempParticle = new PlumeParticle();
			tempParticle.x = cmpnd.getDouble(tag+"x");
			tempParticle.y = cmpnd.getDouble(tag+"y");
			tempParticle.z = cmpnd.getDouble(tag+"z");
			tempParticle.vx = cmpnd.getDouble(tag+"vx");
			tempParticle.vy = cmpnd.getDouble(tag+"vy");
			tempParticle.vz = cmpnd.getDouble(tag+"vz");
			tempParticle.dvy = cmpnd.getDouble(tag+"dvy");
			
			if(tempParticle.x==tempParticle.y&&tempParticle.x==tempParticle.z&&tempParticle.x==0){
				return null;
			}
			return tempParticle;
			
		}
		
		public void writeToNBT(NBTTagCompound cmpnd,String tag){

			cmpnd.setDouble(tag+"x",this.x);
			cmpnd.setDouble(tag+"y",this.y);
			cmpnd.setDouble(tag+"z",this.z);
			cmpnd.setDouble(tag+"vx",this.vx);
			cmpnd.setDouble(tag+"vy",this.vy);
			cmpnd.setDouble(tag+"vz",this.vz);
			cmpnd.setDouble(tag+"dvy",this.dvy);
		}
		
		}
		
	public static class Vect
	 {
		 public double x,y,z,i,j,k,r;
		 public boolean bool, bool2;
		 
		 public int var1,var2,var3, var4 =0;
		 
		 public Vect(double x, double y, double z)
		 {
			 this.x = x;
			 this.y = y;
			 this.z = z;
		 }
		 
		 public Vect(double x, double y, double z, double t, double h, double e, double r)
		 {
			 this.x = x;
			 this.y = y;
			 this.z = z;
			 this.i = t;
			 this.j = h;
			 this.k = e;
			 this.r = r;
		 }
		 
		 public Vect(double[] vec,  double i, double j, double k, double r, boolean bool)
		 {
			 double[] dir = LinearAlgebra.vectorNormalize(vec);
			 this.x = dir[0];
			 this.y = dir[1];
			 this.z = dir[2];
			 this.i = i;
			 this.j = j;
			 this.k = k;
			 this.r = r;
			 this.bool = bool;
		 }
		 
		 public Vect(){}
		 
			public static Vect readFromNBT(NBTTagCompound cmpnd, String tag)
			{
				Vect tempVect = new Vect();
				tempVect.x = cmpnd.getDouble(tag+"x");
				tempVect.y = cmpnd.getDouble(tag+"y");
				tempVect.z = cmpnd.getDouble(tag+"z");
				tempVect.i = cmpnd.getDouble(tag+"t");
				tempVect.j = cmpnd.getDouble(tag+"h");
				tempVect.k = cmpnd.getDouble(tag+"e");
				tempVect.r = cmpnd.getDouble(tag+"r");
				tempVect.bool = cmpnd.getBoolean(tag+"bool");
				tempVect.var1 = cmpnd.getInteger(tag+"var1");
				tempVect.var2 = cmpnd.getInteger(tag+"var2");
				tempVect.var3 = cmpnd.getInteger(tag+"var3");
				tempVect.var4 = cmpnd.getInteger(tag+"var4");
				
				if(tempVect.x==tempVect.y&&tempVect.x==tempVect.z&&tempVect.x==0){
					return null;
				}
				return tempVect;
			}
			
			public void writeToData(DataOutputStream dos)
			{
				try {
					
					dos.writeDouble(x);
					dos.writeDouble(y);
					dos.writeDouble(z);
					dos.writeDouble(i);
					dos.writeDouble(j);
					dos.writeDouble(k);
					dos.writeDouble(r);

					dos.writeInt(var1);
					dos.writeInt(var2);
					dos.writeInt(var3);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			public static Vect readFromData(ByteArrayDataInput dat)
			{
				Vect tempVect = new Vect();
				
				tempVect.x = dat.readDouble();
				tempVect.y = dat.readDouble();
				tempVect.z = dat.readDouble();
				tempVect.i = dat.readDouble();
				tempVect.j = dat.readDouble();
				tempVect.k = dat.readDouble();
				tempVect.r = dat.readDouble();
				tempVect.var1 = dat.readInt();
				tempVect.var2 = dat.readInt();
				tempVect.var3 = dat.readInt();
				
				return tempVect;
			}
			
			public void writeToNBT(NBTTagCompound cmpnd,String tag){

				cmpnd.setDouble(tag+"x",this.x);
				cmpnd.setDouble(tag+"y",this.y);
				cmpnd.setDouble(tag+"z",this.z);
				cmpnd.setDouble(tag+"t",this.i);
				cmpnd.setDouble(tag+"h",this.j);
				cmpnd.setDouble(tag+"e",this.k);
				cmpnd.setDouble(tag+"r",this.r);
				cmpnd.setBoolean(tag+"bool",this.bool);
				cmpnd.setInteger(tag+"var1", var1);
				cmpnd.setInteger(tag+"var2", var2);
				cmpnd.setInteger(tag+"var3", var3);
				cmpnd.setInteger(tag+"var4", var4);
			}
			
			
			public String toString()
			{
				return "x: "+Double.toString(x)+
					" y: "+Double.toString(y)+
					" z: "+Double.toString(z)+
					" t: "+Double.toString(i)+
					" h: "+Double.toString(j)+
					" e: "+Double.toString(k)+
					" r: "+Double.toString(r)+
					" bool: "+Boolean.toString(bool);
			}
	 }
	  
}
