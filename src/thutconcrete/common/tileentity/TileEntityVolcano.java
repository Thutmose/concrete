package thutconcrete.common.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.Block16Fluid;
import thutconcrete.common.blocks.BlockBoom;
import thutconcrete.common.blocks.BlockDust;
import thutconcrete.common.blocks.BlockLava;
import thutconcrete.common.blocks.BlockSolidLava;
import thutconcrete.common.blocks.BlockVolcano;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.LinearAlgebra;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;

public class TileEntityVolcano extends TileEntity
{
	
	public int typeid = 10;
	public int height = 0;
	public int z;
	public int ventCount = 0;
    int n=0;
    public static List<Integer> replaceable =new ArrayList<Integer>();
    public static List<Integer> lava = new ArrayList<Integer>();
    public static List<Integer> solidlava = new ArrayList<Integer>();
    public boolean firstTime = true;
    public boolean erupted = false;
    public boolean active = true;
    public static int ashAmount = ConfigHandler.ashAmount;;
    
    public String[] types = 
    	{
    		"Mafic",
    		"Intermediate",
    		"Felsic",
    	};
    
    public int[][] sides = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1},{0,0}};
    
    private List<Vect> sideVents = new ArrayList<Vect>();
    private Vect mainVent = new Vect(0,1,0);
    
    private static ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
    private static LinearAlgebra vec;
	public List<PlumeParticle> particles = new ArrayList<PlumeParticle>();
	public List<PlumeParticle> deadParticles = new ArrayList<PlumeParticle>();
	boolean doop = false;
	
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
	@Override
	public void updateEntity()
	{
		if(!worldObj.isRemote&&ConfigHandler.volcanosActive)
		{
			if(firstTime)
			{
				init();
			}
			if(!dormant)
			{
				volcanoTick();
				plumeTick();
				maintainMagma();
				if(rand.nextGaussian()>6)
				{
					dormant = true;
				}
			}
			else if(rand.nextGaussian()>4.5)
			{
				dormant = false;
			}
		}
	}
	
	private void init()
	{
		firstTime = false;
		if(typeid>2)
		{
			height = ConcreteCore.getVolcano(xCoord, z);
			typeid = height>40?2:height>20?1:0;
			r0 = height/2;
			n = ashAmount*(typeid+1);
			ventCount = (int) (10*Math.random());
			mainVent.i = height+64-yCoord;
			mainVent.r = ConfigHandler.CoolRate;
			mainVent.k = 40;
			mainVent.j = 0;
			mainVent.bool = false;
			mainVent.var = 0;
			for(int i = 0; i<ventCount; i++)
			{
				sideVents.add(new Vect(new double[] {2*(Math.random()-0.5), Math.random(), 2*(Math.random()-0.5)},
				height, mainVent.i*Math.random(), Math.random()*10, 0.85*mainVent.r , true));
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
		}
		
		if(!lava.contains(BlockLava.getInstance(0).blockID))
		{

			for(Block block:Block.blocksList){
				if(block!=null){
				String name = block.getUnlocalizedName();
				if(block.getUnlocalizedName().toLowerCase().contains("ore")
						||block.getUnlocalizedName().toLowerCase().contains("dirt")	
						||block.getUnlocalizedName().toLowerCase().contains("sand")	
						||block.getUnlocalizedName().toLowerCase().contains("stone")		
						||block.getUnlocalizedName().toLowerCase().contains("chalk")	
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
	
	private void setLava(double x, double y, double z)
	{
		if(!worldObj.isRemote)
		{
			for(int[] side : sides)
			{
				int id = worldObj.getBlockId((int)x+side[0], (int)y, (int)z+side[1]);
				
				if((replaceable.contains(id)||solidlava.contains(id)))
				{
					safe.notAsSafeSet(worldObj, x+side[0], y, z+side[1], BlockLava.getInstance(typeid).blockID, 0);
				}
				else if(lava.contains(id))
				{
					int meta = worldObj.getBlockMetadata((int)x+side[0], (int)y, (int)z+side[1]);
					if(meta!=0)
					{
						safe.notAsSafeSet(worldObj, x+side[0], y, z+side[1], BlockLava.getInstance(typeid).blockID, 0);
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
	   for(int i = 0; i<ventCount; i++)
	   {
		   Vect vec = sideVents.get(i);
		   vec.writeToNBT(par1, Integer.toString(i));
	   }
	   mainVent.writeToNBT(par1, "main");
	   par1.setBoolean("active", active);
	   par1.setBoolean("erupted", erupted);
	   par1.setBoolean("dormant", dormant);
   }

   public void readFromNBT(NBTTagCompound par1)
   {
      super.readFromNBT(par1);
      typeid = par1.getInteger("type");
      height = par1.getInteger("h");
      z = par1.getInteger("z location");
      ventCount = par1.getInteger("veinCount");
	   for(int i = 0; i<ventCount; i++)
	   {
		   sideVents.add(Vect.readFromNBT(par1,Integer.toString(i)));
	   }
	   mainVent = Vect.readFromNBT(par1, "main");
	   mainVent.var = 0;
	   active = par1.getBoolean("active");
	   erupted = par1.getBoolean("erupted");
	   dormant = par1.getBoolean("dormant");
	   System.out.println("Loaded "+types[typeid]+" Volcano at location "+xCoord+" "+z);
	   ConcreteCore.getVolcano(xCoord, z);
   }

   public int getZCoord()
   {
	   return z;
   }
   
   private void volcanoTick()
	   {
		   if(active&&Math.random()>0.975)
			{
			  if(ventCount>0)
			  {
				growVent(sideVents.get(index));
				index = (index+1)%sideVents.size();
			  }
			  growVent(mainVent);
			  if(Math.random()>0.95)
			  {
				active = false;
			  }
			}
			
		  else if (!active&&Math.random()>0.9999)
		   {
			   active = true;
		   }
	   }
	      
   private void plumeTick()
	   {
	   int y = worldObj.getTopSolidOrLiquidBlock(xCoord, z);
	   int id = worldObj.getBlockId(xCoord, y, z);
		   if(erupted&&ashAmount>1000&&(lava.contains(id)||solidlava.contains(id)))
		   {
			   plumeCalculations();
		   }
	   }

	private void plumeCalculations()
		{
		
		if(particles.size()==0)
		{
			erupted = false;
			active = true;
			return;
		}
		
		for(PlumeParticle p: particles)
		{
			int y = (int)p.y;//1+worldObj.getTopSolidOrLiquidBlock((int)p.x, (int)p.z);
			int id = worldObj.getBlockId((int)p.x, y, (int)p.z);
			
	        boolean canBreak = Block16Fluid.instance.willBreak(id);

	        boolean breakException = Block16Fluid.instance.breakException(BlockDust.instance.blockID, id);
			
			if(id==0||(canBreak&&!breakException))
				worldObj.setBlock((int)p.x, y, (int)p.z, BlockDust.instance.blockID, (int)p.vx, 3);
		}
		
		particles.clear();
		
	}
	
	private void addPlumeParticles()
	{
		int typeFactor = typeid == 2? 255: typeid==1? 200: 150;
		Random r = new Random();
		r.setSeed(seed);
		r0 = Math.min(r0,50); //Limits the size to "50"
		while(n>0)
		{
			double x,z, dx, dz, h;
		
			x = r0*r.nextGaussian();
			z = r0*r.nextGaussian();	
			
			dx = safe.getWind(worldObj, x, z)[0];
    		dz = safe.getWind(worldObj, x, z)[1];
	    	

			int y=typeFactor-r.nextInt(50);
			h = y-1;	
			
	    	
	    	int id1 = safe.safeGetID(worldObj,x+(int)(dx*(y-h)), h, z+(int)(dz*(y-h)));
	    	boolean fell = false;
	    	
	    	while(h>1)
	    	{
	    		id1 = safe.safeGetID(worldObj,x+(int)(dx*(1+y-h)), h-1, z+(int)(dz*(1+y-h)));
	    		if(!(id1==0||Block16Fluid.instance.breaks.contains(id1)))
	    		{
	    			break;
	    		}
	    		h--;
	    	}
	    	
			
			PlumeParticle particle = new PlumeParticle();
			
			particle.x = x+x0;
			particle.y = h;
			particle.z = z+z0;
			
			particle.vx = num;
			
			particles.add(particle);
			
			n--;
		}
		

	}
	
	
	void growVent(Vect vent)
	{
		double maxLength = vent.i;
		
		double x = vent.x,y = vent.y,z = vent.z, h = vent.j, e = vent.k, r = vent.r;
		if(vent!=mainVent && h>mainVent.var)
		{
			return;
		}

		int i = 1;
		int id = 1;

		boolean toErupt = false;
		
		while(id!=0)
		{
			i++;
			id = safe.safeGetID(worldObj, xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z);
			
			if(!(lava.contains(id)||replaceable.contains(id)||solidlava.contains(id))) break;
			
			setLava(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z);
			if(vent == mainVent)
			{
				mainVent.var = (int) (i*y+h);
			}
			if(yCoord+i*y+h>64&&rand.nextGaussian()>r&&!erupted)
			{
				toErupt = true;
			}
		}
		
		if(mainVent.var > 0.75*h&&rand.nextGaussian()>2)
		{
			active = false;
		}
		

	  if(Math.random()>0.95)
	  {
			ExplosionCustom boom = new ExplosionCustom();
	    	boom.doExplosion(worldObj,xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, 0.1*Math.random()*e*(typeid+1), false);
	    	worldObj.playSoundEffect(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, "random.explode", 10.0F, 1.0F);
	  }
		
		if(toErupt||(vent == mainVent && doop))
		{
			ExplosionCustom boom = new ExplosionCustom();
	    	boom.doExplosion(worldObj,xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, Math.random()*e*(typeid+1), false);
	    	worldObj.playSoundEffect(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, "random.explode", 10.0F, 1.0F);
	    	x0 = xCoord+i*x; y0 =  yCoord+i*y+h; z0 = getZCoord()+i*z;
	    	r0 = e*(typeid+1);
	    	n = (int) (e*(typeid+1)*100);
	    	addPlumeParticles();
	    	doop = false;
	    	if(!vent.bool)
	    	{
		    	erupted = true;
	    	}
		}
		
	}

	void maintainMagma()
	{
		if(yCoord>=40) return;
		
		for(int h = yCoord+1; h<40; h++)
		{
			int id = worldObj.getBlockId(xCoord, h, z);
			if(!(lava.contains(id)||replaceable.contains(id)||solidlava.contains(id))) break;
			
			setLava(xCoord,h, getZCoord());
		}
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
		 public boolean bool;
		 
		 public int var;
		 
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
				tempVect.var = cmpnd.getInteger(tag+"var1");
				
				if(tempVect.x==tempVect.y&&tempVect.x==tempVect.z&&tempVect.x==0){
					return null;
				}
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
				cmpnd.setInteger(tag+"var1", var);
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
