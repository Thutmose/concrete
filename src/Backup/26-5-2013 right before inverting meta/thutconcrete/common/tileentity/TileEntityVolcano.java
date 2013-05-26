package thutconcrete.common.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.common.ConcreteCore;
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
    
    public int[][] sides = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1},{0,0}};
    
    private List<Vect> sideVents = new ArrayList<Vect>();
    private Vect mainVent = new Vect(0,1,0);
    
    private static ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
    private static LinearAlgebra vec;
	public List<PlumeParticle> particles = new ArrayList<PlumeParticle>();
	public List<PlumeParticle> deadParticles = new ArrayList<PlumeParticle>();
	
	//private int[][] ash = new int[10000][3];
	
	private double x0,y0,z0;
	private boolean read = false;
	public double r0 = 0;
	public int seed = new Random().nextInt(1000);
	private double[] wind = {0,0};
	private int num = 8;
	private int dustId = BlockDust.instance.blockID;
	double rMax;
	public boolean first = true;
	public int time = 0;
	Random rand = new Random();
	int index = 0;
	@Override
	public void updateEntity()
	{
		if(!worldObj.isRemote)
		{
			if(firstTime)
			{
				init();
			}
			volcanoTick();
			plumeTick();
		}
	}
	
	private void init()
	{
		firstTime = false;
		if(typeid>2)
		{
			Random r = new Random();
			height = ConcreteCore.getVolcano(xCoord, z);
			typeid = height>60?2:height>30?1:0;
			r0 = height/2;
			n = 50000*(typeid+1);
			ventCount = (int) (10*Math.random());
			mainVent.i = height+64-yCoord;
			mainVent.r = ConfigHandler.CoolRate*ConfigHandler.CoolRate;
			mainVent.k = 40;
			mainVent.j = 0;
			mainVent.bool = false;
			mainVent.var = 0;
			for(int i = 0; i<ventCount; i++)
			{
				sideVents.add(new Vect(new double[] {2*(Math.random()-0.5), Math.random(), 2*(Math.random()-0.5)},
				height, mainVent.i*Math.random(), Math.random()*10,1-Math.random()/250 , true));
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
						){
					replaceable.add(block.blockID);
				}}
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
				
				if((lava.contains(id)||replaceable.contains(id)||solidlava.contains(id)))
					safe.notAsSafeSet(worldObj, x+side[0], y, z+side[1], BlockLava.getInstance(typeid).blockID, 15);
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
	   
	   
   }

   public int getZCoord()
   {
	   return z;
   }
   
   private void volcanoTick()
	   {
		   if(Math.random()>0.975)
			{
			  if(ventCount>0)
			  {
				growVent(sideVents.get(index));
				index = (index+1)%sideVents.size();
			  }
			  if(active)
			  {
				growVent(mainVent);
			  }
			  else if (Math.random()>0.995)
			   {
				   active = true;
			   }
			}
	   }
	      
	private void plumeTick()
	   {
		   if(erupted)
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
			
			{
				
			for(PlumeParticle particle : particles)
			{
				double 	x = particle.x,
						y = particle.y,
						z = particle.z,
						vx = particle.vx,
						vy = particle.vy,
						vz = particle.vz;
				
				if(safe.safeGetID(worldObj, x, y, z)==BlockDust.instance.blockID)
				{
					safe.notAsSafeSet(worldObj, x,y,z,0,0);
				}
				
			//	System.out.println(particle.x+" "+particle.y+" "+particle.z);
				
				double vMag = vec.vectorMag(new double[] {vx,vy,vz});
				double[] vHat = vec.vectorNormalize(new double[] {vx,vy,vz});
				
				double[] current = {x,y,z};
				double[] next = vec.findNextSolidBlock(worldObj, current, vHat, vMag);
		
				
				if(!(next[0]==-1&&next[1]==-1&&next[2]==-1)&&!(next[0]==x0&&next[1]==y0&&next[2]==z0))
				{
					int x1 = (int) (next[0]-vHat[0]), y1 = (int) (next[1]-vHat[1]), z1=(int)(next[2]-vHat[2]);
					safe.safeLookUp(worldObj, x1, y1, z1);
					int id = safe.ID;
					int meta = safe.meta;
					Block block = Block.blocksList[id];
					if(id == BlockBoom.instance.blockID)
					{
						
					}
					else if(id==0)
					{//If 1 ahead is air, add a fallout stack
						deadParticles.add(particle);
						
						//only place/modify on server
						if(!worldObj.isRemote)
						{
							safe.safeSet(worldObj,x1, y1, z1, BlockDust.instance.blockID, num);
						}
						
						
					}
					else if(id==BlockDust.instance.blockID)
					{ //if 1 ahead is fallout
						if((meta+num) < 15){ //if the fallout is not full, add a stack
							deadParticles.add(particle);
							
							//only place/modify on server
							if(!worldObj.isRemote)
							{
								safe.safeSetMeta(worldObj,x1, y1, z1, meta+num);
							}
							
							
						}else
						{ //if the fallout is full, put fallout where currently is
							deadParticles.add(particle);
							safe.safeLookUp(worldObj,x1-2*vHat[0], y1-2*vHat[1], z1-2*vHat[2]);
							//only place/modify on server
							if(!worldObj.isRemote&&safe.ID==0)
							{
								safe.safeSet(worldObj,x1-2*vHat[0], y1-2*vHat[1], z1-2*vHat[2], BlockDust.instance.blockID, num);
							}
							else if(!worldObj.isRemote&&safe.ID==BlockDust.instance.blockID)
							{
								safe.safeSetMeta(worldObj,x1-2*vHat[0], y1-2*vHat[1], z1-2*vHat[2], meta+num);
							}
						}
					}
				}
			
				
				x = x + vx;
				y = y + vy;
				z = z + vz;
				vy -= particle.dvy;
				this.wind = safe.getWind(worldObj, x, z);
				particle.x = x;
				particle.y = y;
				particle.z = z;
				particle.vx = vx*0.9 - 0.01*vx + wind[0]*(y/r0)*(y/r0);
				particle.vy = vy*0.9 - 0.01*vy;
				particle.vz = vz*0.9 - 0.01*vz + wind[1]*(y/r0)*(y/r0);
				
			}
			for(PlumeParticle p : deadParticles)
			{
				particles.remove(p);
			}
		}
		deadParticles.clear();
			
			
	}
	
	private void addPlumeParticles()
	{
		Random r = new Random();
		r.setSeed(seed);
		r0 = Math.min(r0,50); //Limits the size to "50"
		for(int i = 0; i<n/(num+1); i++)
		{
			double x=10,y=10,z=10;
			while(Math.sqrt(x*x+y*y+z*z)>1)
			{
				x = r.nextGaussian();
				y = r.nextGaussian();
				z = r.nextGaussian();
			}
			PlumeParticle particle = new PlumeParticle();
			double[] vect = new double[] {x,y,z};
			double[] vectHat = vec.vectorNormalize(vect);
			double rho = Math.sqrt((x*x+y*y));
			double	vx = 0.75*(rho>0.3?vectHat[0]*Math.exp((rho)*(rho)*(rho)):vectHat[0]/2-2*z/5)*Math.sqrt(10*r0/25),
					vy = 0.75*(rho>0.3?vectHat[1]*Math.exp((rho)*(rho)*(rho)):vectHat[1]/2-2*z/5)*Math.sqrt(10*r0/25),
					vz = 1.5*(Math.random()*Math.random()+(rho>0.3?Math.pow((vx*vx+vy*vy),1/5):(vectHat[2])*(vectHat[2])))*Math.sqrt(10*r0/49);
			particle.x = vectHat[0]+x0;
			particle.y = vectHat[2]+y0;
			particle.z = vectHat[1]+z0;
			particle.vx = vx;
			particle.vy = vz;
			particle.vz = vy;
			particle.dvy = 0.01*(r.nextDouble()+r.nextDouble());
			particles.add(particle);
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
			
			if(Math.random()<r&&!erupted)
			{
				toErupt = true;
			}
		}
		
		if(toErupt)
		{
			ExplosionCustom boom = new ExplosionCustom();
	    	boom.doExplosion(worldObj,xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, Math.random()*e*(typeid+1), false);
	    	worldObj.playSoundEffect(xCoord+i*x, yCoord+i*y+h, getZCoord()+i*z, "random.explode", 10.0F, 1.0F);
	    	x0 = xCoord+i*x; y0 =  yCoord+i*y+h; z0 = getZCoord()+i*z;
	    	r0 = e*(typeid+1);
	    	n = (int) (e*(typeid+1)*100);
	    	addPlumeParticles();
	    	if(!vent.bool)
	    	{
		    	erupted = true;
	    	}
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
		
		public static NBTTagCompound writeToNBT(NBTTagCompound cmpnd,String tag ,PlumeParticle p){

			cmpnd.setDouble(tag+"x",p.x);
			cmpnd.setDouble(tag+"y",p.y);
			cmpnd.setDouble(tag+"z",p.z);
			cmpnd.setDouble(tag+"vx",p.vx);
			cmpnd.setDouble(tag+"vy",p.vy);
			cmpnd.setDouble(tag+"vz",p.vz);
			cmpnd.setDouble(tag+"dvy",p.dvy);

			return cmpnd;
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
		 
		 public Vect(double[] vec,  double t, double h, double e, double r, boolean bool)
		 {
			 double[] dir = LinearAlgebra.vectorNormalize(vec);
			 this.x = dir[0];
			 this.y = dir[1];
			 this.z = dir[2];
			 this.i = t;
			 this.j = h;
			 this.k = e;
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
