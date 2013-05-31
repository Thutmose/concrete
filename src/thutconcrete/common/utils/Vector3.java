package thutconcrete.common.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class Vector3 
{
	double x,  y,  z;
	final int length = 3;
	
	public static double sqrt3 = Math.sqrt(3);
	public static double sqrt2 = Math.sqrt(2);
	public static double pi = Math.PI;

	public static final Vector3 secondAxis = new Vector3(0,1,0);
	public static final Vector3 firstAxis =  new Vector3(1,0,0);
	public static final Vector3 thirdAxis =  new Vector3(0,0,1);
	
	public Vector3(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3()
	{
		this.x = this.y = this.z = 0;
	}
	
	
	public double get(int i)
	{
		assert(i<3);
		return i==0?x:i==1?y:z;
	}
	
	public void set(int i, double j)
	{
		if(i==0)
		{
			x=j;
		}
		else if(i==1)
		{
			y=j;
		}
		else if(i==2)
		{
			z=j;
		}
	}
	
	public void add(int i, double j)
	{
		if(i==0)
		{
			x+=j;
		}
		else if(i==1)
		{
			y+=j;
		}
		else if(i==2)
		{
			z+=j;
		}
	}
	
	public double[] getLocationArray()
	{
		return new double[] {x,y,z};
	}
	
	public Block getBlock(World worldObj)
	{
		return Block.blocksList[worldObj.getBlockId(intX(), intY(), intZ())];
	}
	
	public int getBlockId(World worldObj)
	{
		return worldObj.getBlockId(intX(), intY(), intZ());
	}
	
	public int intX()
	{
		return (x>0?(int)x:(int)x-1);
	}
	
	public int intY()
	{
		return (y>0?(int)y:(int)y-1);
	}
	
	public int intZ()
	{
		return (z>0?(int)z:(int)z-1);
	}
	
	public static Vector3 ArrayTo(double[] a)
	{
		assert(a.length==3);
		return new Vector3(a[0],a[1],a[2]);
	}
	
	public static Vector3 ArrayTo(Double[] a)
	{
		assert(a.length==3);
		return new Vector3(a[0],a[1],a[2]);
	}
	
	public double[] toArray()
	{
		return new double[] {x,y,z};
	}
	
	private ThreadSafeWorldOperations look = new ThreadSafeWorldOperations();
	
	  /**
	   * Returns the unit vector in with the same direction as vector.
	   * @param vector
	   * @return unit vector in direction of vector.
	   */
	  public static Vector3 vectorNormalize(Vector3 vector){
			double vmag = vectorMag(vector);
		  	Vector3 vhat = vectorScalarMult(vector, 1/vmag);
			return vhat;
	  }
	  
	  /**
	   * Returns the unit vector in with the same direction as vector.
	   * @param vector
	   * @return unit vector in direction of vector.
	   */
	  public Vector3 vectorToSpherical(Vector3 vector, boolean minecraft){
		  Vector3 vectorSpher = new Vector3();
		  vectorSpher.x = vectorMag(vector);
		  vectorSpher.y = Math.acos(vector.get(minecraft?1:2)/vectorSpher.x)-pi/2;
		  vectorSpher.z = Math.atan2(vector.get(minecraft?2:1),vector.x);
		  //TODO
		  return vectorSpher;
	  }
	  
	  private Vector3 horizonalPerp(Vector3 vector)
	  {
		  Vector3 vectorH = new Vector3(vector.x, 0, vector.y);
		  return vectorRotateAboutLine(vectorH, secondAxis, pi/2);
	  }
	  
	  /**
	   * Adds vectorA to vectorB
	   * @param vectorA
	   * @param vectorB
	   * @return
	   */
	  public static Vector3 vectorAdd(Vector3 vectorA, Vector3 vectorB){
		  	Vector3 vectorC = new Vector3();
			for(int i=0; i < vectorA.length; i++){vectorC.set(i, vectorA.get(i)+vectorB.get(i));}
			return vectorC;
	  }
	  
	  /**
	   * Subtracts vectorB from vectorA
	   * @param vectorA
	   * @param vectorB
	   * @return
	   */
	  public Vector3 vectorSubtract(Vector3 vectorA, Vector3 vectorB){
		  	Vector3 vectorC = new Vector3();
			for(int i=0; i < vectorA.length; i++){vectorC.set(i, vectorA.get(i)-vectorB.get(i));}
			return vectorC;
	  }
	  
	  public double moduloPi(double num)
	  {
		  double newnum = num;
		  if(num>pi)
		  {
			  while(newnum>pi)
			  {
			//	  System.out.println(num+" "+newnum+" "+(newnum-pi));
				  newnum-=pi;
			  }
		  }
		  else if(num<-pi)
		  {
			  while(newnum<-pi)
			  {
			//	  System.out.println(num+" "+newnum+" "+(newnum+pi));
				  newnum+=pi;
			  }
		  }
		  return newnum;
	  }
	  
	  /**
	   * Returns the magnitude of vector
	   * @param vector
	   * @return
	   */
	  public static double vectorMag(Vector3 vector){
			double vmag = 0;
			for(int i=0; i < 3; i = i+1){vmag = vmag + vector.get(i)*vector.get(i);}
			vmag = Math.sqrt(vmag);
			return vmag;
	  }
	  
	  /**
	   * Returns the magnitude of vector squared
	   * @param vector
	   * @return
	   */
	  public double vectorMagSq(Vector3 vector){
			double vmag = 0;
			for(int i=0; i < vector.length; i = i+1){vmag = vmag + vector.get(i)*vector.get(i);}
			return vmag;
	  }
	  
	  /**
	   * Multiplies the vector by the constant.
	   * @param vector
	   * @param constant
	   * @return
	   */
	  public static Vector3 vectorScalarMult(Vector3 vector, double constant){
			Vector3 newVector = new Vector3();
		  	for(int i=0; i < vector.length; i = i+1){newVector.set(i, constant*vector.get(i));}
			return newVector;
	  }
	    
	  /**
	   * Left multiplies the Matrix by the Vector
	   * @param Matrix
	   * @param vector
	   * @return
	   */
	  public Vector3 vectorMatrixMult(Matrix3 Matrix, Vector3 vector){
		  Vector3 newVect = new Vector3();
		  for(int i=0;i<3;i++){
			  for(int j=0;j<vector.length;j++){
				  newVect.add(i, Matrix.get(i).get(j) * vector.get(j));
			  }
		  }
		  return newVect;
	  }
	  
	  /**
	   * Reflects the vector off the corresponding plane.  The plane vector is the coefficient
	   * a,b,c of ax + by + cz = 0
	   * @param vector
	   * @param plane
	   * @return
	   */
	  public Vector3 vectorReflect(Vector3 vector, Vector3 plane){
		  Vector3 ret;
		  double 	a = plane.x,
				  	b = plane.y,
				  	c = plane.z;
		  double vMag = vectorMag(vector);
		  ret = vectorNormalize(vector);
		  Matrix3 Tmatrix = new Matrix3(new double[]{1-2*a*a,-2*a*b,-2*a*c},
				  new double[]{-2*a*b,1-2*b*b,-2*b*c},
				  new double[]{-2*c*a,-2*c*b,1-2*c*c});
		  ret = vectorMatrixMult(Tmatrix, vector);
		  return vectorScalarMult(ret,vMag);
	  }
	  
	  /**
	   * Rotates the given vector around the given line by the given angle.
	   * This internally normalizes the line incase it is not already normalized
	   * @param vectorH
	   * @param line
	   * @param angle
	   * @return
	   */
	  public Vector3 vectorRotateAboutLine(Vector3 vector, Vector3 line, double angle){
		  line = vectorNormalize(line);
		  Vector3 ret;
		  Matrix3 TransMatrix = new Matrix3();
		  
		  TransMatrix.get(0).x = line.get(0)*line.get(0)*(1-Math.cos(angle))+Math.cos(angle);
		  TransMatrix.get(0).y = line.get(0)*line.get(1)*(1-Math.cos(angle))-line.get(2)*Math.sin(angle);
		  TransMatrix.get(0).z = line.get(0)*line.get(2)*(1-Math.cos(angle))+line.get(1)*Math.sin(angle);
		  
		  TransMatrix.get(1).x = line.get(1)*line.get(0)*(1-Math.cos(angle))+line.get(2)*Math.sin(angle);
		  TransMatrix.get(1).y = line.get(1)*line.get(1)*(1-Math.cos(angle))+Math.cos(angle);
		  TransMatrix.get(1).z = line.get(1)*line.get(2)*(1-Math.cos(angle))-line.get(0)*Math.sin(angle);
		  
		  TransMatrix.get(2).x = line.get(2)*line.get(0)*(1-Math.cos(angle))-line.get(1)*Math.sin(angle);
		  TransMatrix.get(2).y = line.get(2)*line.get(1)*(1-Math.cos(angle))+line.get(0)*Math.sin(angle);
		  TransMatrix.get(2).z = line.get(2)*line.get(2)*(1-Math.cos(angle))+Math.cos(angle);
		  
		  ret = vectorMatrixMult(TransMatrix, vector);
		  return ret;
	  }
	  
	  /**
	   *  Rotates the given vector by the given amounts of pitch and yaw.
	   * @param vector
	   * @param pitch
	   * @param yaw
	   * @return
	   */
	  public Vector3 vectorRotateAboutAngles(Vector3 vector, double pitch, double yaw){
		  return vectorRotateAboutLine(vectorRotateAboutLine(vector, secondAxis, yaw),horizonalPerp(vector), pitch);
	  }
	  
	  /**
	   * Returns the dot (scalar) product of the two vectors
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public static double vectorDot(Vector3 vector1, Vector3 vector2){
		  double dot = 0;
		  for(int i=0; i<vector1.length;i++){
			  dot += vector1.get(i)*vector2.get(i);
		  }
		  return dot;
	  }
	  
	  /**
	   * Returns the angle between two vectors
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public static  double vectorAngle(Vector3 vector1, Vector3 vector2){
		 return Math.acos(vectorDot(vectorNormalize(vector1),vectorNormalize(vector2)));
	  }
	  
	  /**
	   * Returns the dot (scalar) product of the two vectors
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public  static float vectorComponentDot(float x, float x1, float y, float y1, float z, float z1){
		  return x*x1+y*y1+z*z1;
	  }
	  
	  /**
	   * Swaps the ith and jth element of the vector
	   * useful for converting from x,y,z to x,z,y
	   * @param vector
	   * @param i
	   * @param j
	   * @return
	   */
	  public  static double[] vectorSwap(double[] vector, int i, int j){
		  double temp = vector[i];
		  vector[i] = vector[j];
		  vector[j] = temp;
		  return vector;
	  }
	  
	  /**
	   * Returns the cross (vector) product of the two vectors.
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public  static double[] vectorCross(double[] vector1, double[] vector2){
		  double[] vector3 = new double[] {0,0,0};
		  for(int i=0;i<vector1.length;i++){
			  vector3[i]=vector1[(i+1)%3]*vector2[(i+2)%3]-vector1[(i+2)%3]*vector2[(i+1)%3];
		  }
		  return vector3;
	  }


	  
	  public static Vector3 findMidPoint(List<Double[]> points){
		  Vector3 mid = new Vector3();
		  for(int j=0;j<points.size();j++){
			  mid=vectorAdd(Vector3.ArrayTo(points.get(j)),mid);
		  }
		  if(points.size()!=0){
		  mid = vectorScalarMult(mid, 1/points.size());
		  }
		  return mid;
	  }
	  
	  public  static double distBetween(Vector3 pointA, double[] pointB){
		  return vectorMag(vectorSubtract(pointA,pointB));
	  }
	  
	  public  static double distToEntity(double[] pointA, Entity target)
	  {
		  if(target!=null)
		  return vectorMag(vectorSubtract(pointA, new double[]{target.posX,target.posY,target.posZ}));
		  return 0;
	  }
	  
	  public double[] vectorCopy(double[] vector){
		  double[] newVector = new double[vector.length];
		  for(int i=0;i<vector.length;i++){
			  newVector[i]=vector[i];
		  }
		  return newVector;
	  }
	  
	  
	  public static double[] makePrimative(Double[] array){
		  double[] primative = new double[array.length];
		  for(int i=0;i<array.length;i++){
			  primative[i]=array[i];
		  }
		  return primative;
	  }
	  
	  /**
	   * Locates the first solid block in the line indicated by the direction vector, starting from the source
	   * if range is given as 0, it will check out to 320 blocks.
	   * @param worldObj
	   * @param source
	   * @param direction
	   * @param range
	   * @return
	   */
	  public double[] findNextSolidBlock(World worldObj,final double[] source,final double[] direction, double range){
		  if(range==0){
			  range = 320;
		  }
		  
		  double[] location = new double[]{-1,-1,-1};
		  
		  for(double i=0;i<range;i+=0.0625){
			  double  xtest = (source[0]+i*direction[0]),
					  ytest = (source[1]+i*direction[1]),
					  ztest = (source[2]+i*direction[2]);
			  boolean check = look.checkAABB(worldObj,AxisAlignedBB.getBoundingBox(xtest-0.0, ytest-0.0, ztest-0.0,
					  															   xtest+0.0, ytest+0.0, ztest+0.0));
			  
				  if(check){
					  location = new double[] {xtest, ytest, ztest};
					  break;
				  }
			  }
		  return location;
	  }
	
	  public double[] getVectorToEntity(double[] source, Entity target)
	  {
		  if(target!=null)
		  return vectorSubtract(new double[]{target.posX, target.posY, target.posZ}, source);
		  else
			  return new double[]{0,0,0};
	  }
	  
	  /**
	   * determines whether the source can see out as far as range in the given direction.
	   * @param worldObj
	   * @param source
	   * @param direction
	   * @param range
	   * @return
	   */
	  public boolean isVisibleRange(World worldObj, double[] source, double[] direction, double range){
		  
		  direction = vectorNormalize(direction);
		  boolean visible = true;
		  
		  int 	xprev = (int)(source[0]),
				yprev = (int)(source[1]),
				zprev = (int)(source[2]);
		//  range = 20;
		  for(int i=0;i<range;i+=1){
			  int	xtest = (int)(source[0]+i*direction[0]),
					ytest = (int)(source[1]+i*direction[1]),
					ztest = (int)(source[2]+i*direction[2]);
			  if(!(xtest==xprev&&ytest==yprev&&ztest==zprev)){
				  if(look.ID!=0){
					  visible = false;
					  break;
				  }
			  }
			  xprev = xtest;
			  yprev = ytest;
			  zprev = ztest;
		  }
		  return visible;
	  }
	  
	  public boolean isVisibleEntityFromLocation(World worldObj, Entity target, double[] location){
		  boolean[] visible = new boolean[27];
		  if(target==null){return false;}
		  AxisAlignedBB aabb = target.getBoundingBox();
		  boolean ret = false;
		  if(target.isDead){return false;}
		  if(aabb!=null){
			  System.out.println("Checking AABB");
			  for(int i=0;i<3;i++)
				  for(int j=0;j<3;j++)
					  for(int k=0;k<3;k++){
						  if(target.isDead){return false;}
						  double[] targetLoc = {target.posX+(aabb.minX*i/3), target.posZ+(aabb.minZ*k/3), target.posY+(aabb.minY*j/3)};
						  boolean vis = isVisibleLocation(worldObj, location, targetLoc);
						  if(vis) return true;
						  visible[9*i+3*j+k]=isVisibleLocation(worldObj, location, targetLoc);
			  }
			  for(int i=0;i<27;i++){ ret = ret||visible[i];}
		  }else{
			  double[] targetLoc = {target.posX, target.posZ, target.posY+target.height/2};
			  ret = isVisibleLocation(worldObj, location, targetLoc);
		  }
		  return ret;
	  }
	  
	  
	  /**
	   * determines whether the location given is visible from source.
	   * @param worldObj
	   * @param source
	   * @param direction
	   * @param range
	   * @return
	   */
	  public boolean isVisibleLocation(World worldObj, double[] source, double[] location){
		  
		  double[] direction = vectorSubtract(location,source);
		  double range = vectorMag(direction);
		  direction = vectorNormalize(direction);
		  boolean visible = true;
		//  range = 20;
		  for(double i=0;i<range-0.0625;i+=0.0625){
			  double  xtest = (source[0]+i*direction[0]),
					  ztest = (source[1]+i*direction[1]),
					  ytest = (source[2]+i*direction[2]);
			  look.safeLookUp(worldObj,(int)xtest,(int)ytest,(int)ztest);
			  
			  boolean check = look.checkAABB(worldObj,AxisAlignedBB.getBoundingBox(xtest-0.0, ytest-0.0, ztest-0.0,
					  															   xtest+0.0, ytest+0.0, ztest+0.0));
			  
			  if(check&&!look.isLiquid(worldObj,(int)xtest,(int)ytest,(int)ztest)){
				  visible=false;
				  break;
			  }
		  }
		  return visible;
	  }
	  
    public boolean isVisibleEntityFromEntity(Entity looker, Entity target, double rMax)
    {
    	double[] location = {looker.posX, looker.posY, looker.posZ};
    	return isVisibleEntityFromLocation(looker.worldObj, target, location);
    }
  

    //TODO make this not continue checking each pixel if the block or aabb was null.
	public List<Entity> firstEntityOnLine(double range, double[] direction, double[] source, World worldObj, boolean effect)
	{
		int n = 0;
		double xprev = source[0], yprev = source[1], zprev = source[2];
		boolean notNull = true;
		double closest = range;
		double blocked = 0;
		
		for(double i=0;i<range;i+=0.0625)
		{
			  double  xtest = (source[0]+i*direction[0]),
					  ytest = (source[1]+i*direction[1]),
					  ztest = (source[2]+i*direction[2]);
			  
			  boolean check = isPointClearBlocks(xtest, ytest, ztest, worldObj);
			  blocked = Math.sqrt(i*direction[0]*i*direction[0]+i*direction[1]*i*direction[1]+i*direction[2]*i*direction[2]);
			  if(effect&&worldObj.isRemote)
			  {
				  worldObj.spawnParticle("flame",xtest, ytest, ztest, 0, 0, 0);
			  }
			  if(!check){
				  break;
			  }
			  if(!((int)xtest==(int)xprev&&(int)ytest==(int)yprev&&(int)ztest==(int)zprev))
			  {
				  int x0 = (xtest>0?(int)xtest:(int)xtest-1), y0 = (ytest>0?(int)ytest:(int)ytest-1), z0 = (ztest>0?(int)ztest:(int)ztest-1);
				  List<Entity> targets = worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(x0, y0, z0, x0+1, y0+1, z0+1));
				  if(targets!=null&&targets.size()>0)
				  {
					  return targets;
				  }
				  
			  }
			  
			  yprev = ytest; xprev = xtest; zprev = ztest;
		}

		return null;
	}
	
	
	public boolean isEntityVisiblefirstEntityOnLine(Entity target, Vector3 direction, Vector3 source, World worldObj)
	{
		direction = vectorNormalize(direction);
		List<Entity> targets = firstEntityOnLine(distToEntity(source, target), direction, source, worldObj, false);
		return target==null?false:targets==null?false:targets.contains(target);
	}
	  
	public boolean isPointClearBlocks(double x, double y, double z, World worldObj)
	{
		int x0 = (x>0?(int)x:(int)x-1), y0 = (y>0?(int)y:(int)y-1), z0 = (z>0?(int)z:(int)z-1);
		
		Block block = Block.blocksList[worldObj.getBlockId(x0, y0, z0)];
		
		if(block==null)
			return true;
		
		List<AxisAlignedBB> aabbs = new ArrayList();
		block.addCollisionBoxesToList(worldObj, x0, y0, z0, AxisAlignedBB.getBoundingBox(x, y, z, x, y, z), aabbs, null);
		
		//AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(worldObj, x0, y0, z0);
		for(AxisAlignedBB aabb: aabbs)
		{
			if(aabb!=null)
			{
			if(y<=aabb.maxY&&y>=aabb.minY)
				return false;
			if(z<=aabb.maxZ&&z>=aabb.minZ)
				return false;
			if(x<=aabb.maxX&&x>=aabb.minX)
				return false;
			}
		}
		
		return true;
	}
	
	  
	public boolean isPointClearOfaabb(double x, double y, double z, AxisAlignedBB aabb)
	{
		if(y<=aabb.maxY&&y>=aabb.minY)
			return false;
		if(z<=aabb.maxZ&&z>=aabb.minZ)
			return false;
		if(x<=aabb.maxX&&x>=aabb.minX)
			return false;
		
		return true;
	}
	  
	
	public static class Matrix3
	{
		Vector3[] Rows = new Vector3[3];
		int size = 3;
		
		public Matrix3()
		{
			Rows[0] = new Vector3();
			Rows[1] = new Vector3();
			Rows[2] = new Vector3();
		}
		
		public Matrix3(double[] a, double[] b, double[] c)
		{
			Rows[0] = Vector3.ArrayTo(a);
			Rows[1] = Vector3.ArrayTo(b);
			Rows[2] = Vector3.ArrayTo(c);
		}
	
		public Vector3 get(int i)
		{
			assert(i<3);
			return Rows[i];
		}
		
		public double get(int i, int j)
		{
			assert(i<3);
			return Rows[i].get(j);
		}
		
		public void set(int i, Vector3 j)
		{
			assert(i<3);
			Rows[i] = j;
		}
		
		public void set(int i, int j, double k)
		{
			Rows[i].set(j, k);
		}
		
		public double[][] toArray()
		{
			return new double[][] {Rows[0].toArray(),Rows[1].toArray(),Rows[2].toArray()};
		}
		

		  /**
		   * Multiplies MatrixA by MatrixB in the form AB.
		   * @param MatrixA
		   * @param MatrixB
		   * @return
		   */
		  public static Matrix3 matrixMatrixMult(Matrix3 MatrixA, Matrix3 MatrixB){
			  Matrix3 MatrixC = new Matrix3();
			  MatrixB = matrixTranspose(MatrixB);
			  for(int i=0;i<3;i++){
				  for(int j=0;j<3;j++){
					  MatrixC.set(i, j, vectorDot(MatrixA.get(i),MatrixB.get(j)));
				  }
			  }
			  return MatrixC;
		  }
		  
		  /**
		   * Transposes the given Matrix
		   * @param Matrix
		   * @return
		   */
		  public static Matrix3 matrixTranspose(Matrix3 Matrix){
			  Matrix3 MatrixT = new Matrix3();
			  for(int i = 0;i<3;i++){
				  for(int j = 0;j<3;j++){
					  MatrixT.set(i,j,Matrix.get(j, i));
				  }
			  }
			  return MatrixT;
		  }
		  
			 
		  /**
		   * Computes the Inverse of the matrix
		   * @param Matrix
		   * @return
		   */
		  public static Matrix3 matrixInverse(Matrix3 Matrix){
			  Matrix3  Inverse = new Matrix3();
			  double det = matrixDet(Matrix);
			  for(int i=0;i<3;i++){
				  for(int j=0;j<3;j++){
					  Inverse.set(i,j,Math.pow(-1, i+j)*matrixDet(matrixMinor(Matrix,i,j))/det);
				  }
			  }
			  Inverse = matrixTranspose(Inverse);
			  return Inverse;  
		  }
		  

		  /**
		   * Computes the Determinant of the given matrix, Matrix must be square.
		   * @param Matrix
		   * @return
		   */
		  public static double matrixDet(Matrix3 Matrix){
			  double det = 0;
			  int n = Matrix.size;
			  if(n==2){ det = Matrix.get(0,0)*Matrix.get(1,1)-Matrix.get(1,0)*Matrix.get(0,1);}
			  else{
				  for(int i=0;i<n;i++){
					  det+= Math.pow(-1, i)*Matrix.get(0,i)*matrixDet(matrixMinor(Matrix,0,i));
				  }
			  }
			  return det;
		  }
		  
		  /**
		   * Computes the minor matrix formed from removal of the ith row and jth
		   * column of matrix.
		   * @param Matrix
		   * @param i
		   * @param j
		   * @return
		   */
		  public static Matrix3 matrixMinor(Matrix3 input, int i, int j){
			  double[][] Matrix = input.toArray();
			  int n = Matrix.length;
			  int m = Matrix[0].length;
			  Double[][] TempMinor = new Double[m-1][n-1];
			  List<ArrayList<Double>> row = new ArrayList<ArrayList<Double>>();
			  for(int k = 0;k<n;k++){
				  if(k!=i){
					  row.add(new ArrayList<Double>());
					  for(int l=0;l<m;l++){
						  if(l!=j){
						  row.get(k-(k>i?1:0)).add(Matrix[k][l]);
						  }
					  }
				  }
			  }
			  for(int k = 0;k<n-1;k++){
				  TempMinor[k] = row.get(k).toArray(new Double[0]);
			  }
			  Matrix3  Minor = new Matrix3();
			  Minor.size = n-1;
			  for(int k=0;k<n-1;k++){
				  for(int l=0;l<m-1;l++){
					  Minor.set(k,l,TempMinor[k][l]);
				  }
			  }
			  return Minor;
		  }


		  /**
		   * Adds MatrixA and MatrixB
		   * @param MatrixA
		   * @param MatrixB
		   * @return
		   */
		  public static Matrix3  matrixAddition(Matrix3  MatrixA, Matrix3  MatrixB){
			  Matrix3  MatrixC = new Matrix3();
			  for(int i=0;i<3;i++){
				  for(int j=0;j<3;j++){
					  MatrixC.set(i, i,MatrixA.get(i, j)+MatrixB.get(i,j));
				  }
			  }
			  return MatrixC;
		  }
		  
		  /**
		   * Subtracts MatrixB from MatrixA
		   * @param MatrixA
		   * @param MatrixB
		   * @return
		   */
		  public static Matrix3  matrixSubtraction(Matrix3  MatrixA, Matrix3  MatrixB){
			  Matrix3  MatrixC = new Matrix3();
			  for(int i=0;i<3;i++){
				  for(int j=0;j<3;j++){
					  MatrixC.set(i, i,MatrixA.get(i, j)-MatrixB.get(i,j));
				  }
			  }
			  return MatrixC;
		  }
		  
		  /**
		   * Multiplies the Matrix by the scalar
		   * @param Matrix
		   * @param scalar
		   * @return
		   */
		  public static Matrix3  matrixScalarMuli(Matrix3 Matrix, double scalar){
			  Matrix3 ret = new Matrix3();
			  for(int i=0;i<3;i++){
				  for(int j=0;j<3;j++){
					  ret.set(i,j,Matrix.get(i, j)*scalar);
				  }
			  }
			  return ret;
		  }
		
	}
	
}