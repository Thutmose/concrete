package thutconcrete.common.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;


public class LinearAlgebra {
	  
	public static double sqrt3 = Math.sqrt(3);
	public static double sqrt2 = Math.sqrt(2);
	public static double pi = Math.PI;

	public static final double[] secondAxis = {0,1,0};
	public static final double[] firstAxis = {1,0,0};
	public static final double[] thirdAxis = {0,0,1};
	
	private static ThreadSafeWorldOperations look = new ThreadSafeWorldOperations();
	
	  /**
	   * Returns the unit vector in with the same direction as vector.
	   * @param vector
	   * @return unit vector in direction of vector.
	   */
	  public static double[] vectorNormalize(double[] vector){
			double vmag = vectorMag(vector);
		  	double[] vhat = vectorScalarMult(vector, 1/vmag);
			return vhat;
	  }
	  
	  /**
	   * Returns the unit vector in with the same direction as vector.
	   * @param vector
	   * @return unit vector in direction of vector.
	   */
	  public static double[] vectorToSpherical(double[] vector, boolean minecraft){
		  double[] vectorSpher = {0,0,0};
		  vectorSpher[0]=vectorMag(vector);
		  vectorSpher[1] = Math.acos(vector[minecraft?1:2]/vectorSpher[0])-pi/2;
		  vectorSpher[2] = Math.atan2(vector[minecraft?2:1],vector[0]);
		  //TODO
		  return vectorSpher;
	  }
	  
	  private static double[] horizonalPerp(double[] vector)
	  {
		  double[] vectorH = {vector[0], 0, vector[2]};
		  return vectorRotateAboutLine(vectorH, secondAxis, pi/2);
	  }
	  
	  /**
	   * Adds vectorA to vectorB
	   * @param vectorA
	   * @param vectorB
	   * @return
	   */
	  public static double[] vectorAdd(double[] vectorA, double[] vectorB){
		  	double[] vectorC = {0,0,0};
			for(int i=0; i < vectorA.length; i++){vectorC[i] = vectorA[i]+vectorB[i];}
			return vectorC;
	  }
	  
	  /**
	   * Subtracts vectorB from vectorA
	   * @param vectorA
	   * @param vectorB
	   * @return
	   */
	  public static double[] vectorSubtract(double[] vectorA, double[] vectorB){
		  	double[] vectorC = {0,0,0};
			for(int i=0; i < vectorA.length; i++){vectorC[i] = vectorA[i]-vectorB[i];}
			return vectorC;
	  }
	  
	  public static double moduloPi(double num)
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
	  public static double vectorMag(double[] vector){
			double vmag = 0;
			for(int i=0; i < vector.length; i = i+1){vmag = vmag + vector[i]*vector[i];}
			vmag = Math.sqrt(vmag);
			return vmag;
	  }
	  
	  /**
	   * Returns the magnitude of vector squared
	   * @param vector
	   * @return
	   */
	  public static double vectorMagSq(double[] vector){
			double vmag = 0;
			for(int i=0; i < vector.length; i = i+1){vmag = vmag + vector[i]*vector[i];}
			return vmag;
	  }
	  
	  /**
	   * Multiplies the vector by the constant.
	   * @param vector
	   * @param constant
	   * @return
	   */
	  public static double[] vectorScalarMult(double[] vector, double constant){
			double [] newVector = new double[vector.length];
		  	for(int i=0; i < vector.length; i = i+1){newVector[i] = constant*vector[i];}
			return newVector;
	  }
	    
	  /**
	   * Left multiplies the Matrix by the Vector
	   * @param Matrix
	   * @param vector
	   * @return
	   */
	  public static double[] vectorMatrixMult(double[][] Matrix, double[] vector){
		  double [] newVect = {0,0,0};
		  for(int i=0;i<3;i++){
			  for(int j=0;j<vector.length;j++){
				  newVect[i] += Matrix[i][j] * vector[j];
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
	  public static double[] vectorReflect(double[] vector, double[] plane){
		  double 	a = plane[0],
				  	b = plane[1],
				  	c = plane[2];
		  double vMag = vectorMag(vector);
		  vector = vectorNormalize(vector);
		  double [] [] Tmatrix = {{1-2*a*a,-2*a*b,-2*a*c},
				  				  {-2*a*b,1-2*b*b,-2*b*c},
				  				  {-2*c*a,-2*c*b,1-2*c*c}};
		  vector = vectorMatrixMult(Tmatrix, vector);
		  return vectorScalarMult(vector,vMag);
	  }
	  
	  /**
	   * Rotates the given vector around the given line by the given angle.
	   * This internally normalizes the line incase it is not already normalized
	   * @param vector
	   * @param line
	   * @param angle
	   * @return
	   */
	  public static double[] vectorRotateAboutLine(double[] vector, double[] line, double angle){
		  line = vectorNormalize(line);
		  double[][] TransMatrix = new double[3][3];
		  TransMatrix[0][0] = line[0]*line[0]*(1-Math.cos(angle))+Math.cos(angle);
		  TransMatrix[0][1] = line[0]*line[1]*(1-Math.cos(angle))-line[2]*Math.sin(angle);
		  TransMatrix[0][2] = line[0]*line[2]*(1-Math.cos(angle))+line[1]*Math.sin(angle);
		  TransMatrix[1][0] = line[1]*line[0]*(1-Math.cos(angle))+line[2]*Math.sin(angle);
		  TransMatrix[1][1] = line[1]*line[1]*(1-Math.cos(angle))+Math.cos(angle);
		  TransMatrix[1][2] = line[1]*line[2]*(1-Math.cos(angle))-line[0]*Math.sin(angle);
		  TransMatrix[2][0] = line[2]*line[0]*(1-Math.cos(angle))-line[1]*Math.sin(angle);
		  TransMatrix[2][1] = line[2]*line[1]*(1-Math.cos(angle))+line[0]*Math.sin(angle);
		  TransMatrix[2][2] = line[2]*line[2]*(1-Math.cos(angle))+Math.cos(angle);
		  vector = vectorMatrixMult(TransMatrix, vector);
		  return vector;
	  }
	  
	  /**
	   *  Rotates the given vector by the given amounts of pitch and yaw.
	   * @param vector
	   * @param pitch
	   * @param yaw
	   * @return
	   */
	  public static double[] vectorRotateAboutAngles(double[] vector, double pitch, double yaw){
		  return vectorRotateAboutLine(vectorRotateAboutLine(vector, secondAxis, yaw),horizonalPerp(vector), pitch);
	  }
	  
	  /**
	   * Returns the dot (scalar) product of the two vectors
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public static double vectorDot(double[] vector1, double[] vector2){
		  double dot = 0;
		  for(int i=0; i<vector1.length;i++){
			  dot += vector1[i]*vector2[i];
		  }
		  return dot;
	  }
	  
	  /**
	   * Returns the angle between two vectors
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public static double vectorAngle(double[] vector1, double[] vector2){
		 return Math.acos(vectorDot(vectorNormalize(vector1),vectorNormalize(vector2)));
	  }

	  /**
	   * Returns the yaw angle between two vectors
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public static double vectorDYaw(double[] vector1, double[] vector2){
		 return Math.acos(vectorDot(vectorNormalize(new double[]{vector1[0],0,vector1[2]}),vectorNormalize(new double[]{vector2[0],0,vector2[2]})))-pi/2;
	  }
	  /**
	   * Returns the pitch angle between two vectors
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public static double vectorDPitch(double[] vector1, double[] vector2){
		 

		  return 0;
		  
	  }
	  
	  
	  
	  /**
	   * Returns the dot (scalar) product of the two vectors
	   * @param vector1
	   * @param vector2
	   * @return
	   */
	  public static float vectorComponentDot(float x, float x1, float y, float y1, float z, float z1){
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
	  public static double[] vectorSwap(double[] vector, int i, int j){
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
	  public static double[] vectorCross(double[] vector1, double[] vector2){
		  double[] vector3 = new double[] {0,0,0};
		  for(int i=0;i<vector1.length;i++){
			  vector3[i]=vector1[(i+1)%3]*vector2[(i+2)%3]-vector1[(i+2)%3]*vector2[(i+1)%3];
		  }
		  return vector3;
	  }

	  /**
	   * Transposes the given Matrix
	   * @param Matrix
	   * @return
	   */
	  public static double[][] matrixTranspose(double[][]Matrix){
		  int n = Matrix.length;
		  int m = Matrix[0].length;
		  double[][] MatrixT = new double[m][n];
		  for(int i = 0;i<m;i++){
			  for(int j = 0;j<n;j++){
				  MatrixT[i][j]=Matrix[j][i];
			  }
		  }
		  return MatrixT;
	  }
	  
	  /**
	   * Computes the Determinant of the given matrix, Matrix must be square.
	   * @param Matrix
	   * @return
	   */
	  public static double matrixDet(double[][]Matrix){
		  double det = 0;
		  int n = Matrix.length;
		  int m = Matrix[0].length;
		  assert(m==n);
		  if(n==2){ det = Matrix[0][0]*Matrix[1][1]-Matrix[1][0]*Matrix[0][1];}
		  else{
			  for(int i=0;i<n;i++){
				  det+= Math.pow(-1, i)*Matrix[0][i]*matrixDet(matrixMinor(Matrix,0,i));
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
	  public static double[][] matrixMinor(double[][]Matrix, int i, int j){
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
		  double[][] Minor = new double[m-1][n-1];
		  for(int k=0;k<n-1;k++){
			  for(int l=0;l<m-1;l++){
				  Minor[k][l] = TempMinor[k][l];
			  }
		  }
		  return Minor;
	  }
	 
	  /**
	   * Computes the Inverse of the matrix
	   * @param Matrix
	   * @return
	   */
	  public static double[][] matrixInverse(double[][]Matrix){
		  int n = Matrix.length;
		  double[][] Inverse = new double[n][n];
		  double det = matrixDet(Matrix);
		  for(int i=0;i<n;i++){
			  for(int j=0;j<n;j++){
				  Inverse[i][j] = Math.pow(-1, i+j)*matrixDet(matrixMinor(Matrix,i,j))/det;
			  }
		  }
		  Inverse = matrixTranspose(Inverse);
		  return Inverse;
		  
	  }

	  /**
	   * Multiplies MatrixA by MatrixB in the form AB.
	   * @param MatrixA
	   * @param MatrixB
	   * @return
	   * /
	  public static double[][] matrixMatrixMult(double[][] MatrixA, double[][] MatrixB){
		  int n = MatrixA.length;
		  int m = MatrixB[0].length;
		  double[][]MatrixC = new double[m][n];
		  MatrixB = matrixTranspose(MatrixB);
		  for(int i=0;i<n;i++){
			  for(int j=0;j<m;j++){
				  MatrixC[i][j] = vectorDot(MatrixA[i],MatrixB[j]);
			  }
		  }
		  return MatrixC;
	  }
	 // */
	  
	  /**
	   * Adds MatrixA and MatrixB
	   * @param MatrixA
	   * @param MatrixB
	   * @return
	   */
	  public static double[][] matrixAddition(double[][] MatrixA, double[][] MatrixB){
		  int n = MatrixA.length;
		  int m = MatrixB[0].length;
		  double[][] MatrixC = new double[n][m];
		  for(int i=0;i<n;i++){
			  for(int j=0;j<n;j++){
				  MatrixC[i][j] = MatrixA[i][j]+MatrixB[i][j];
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
	  public static double[][] matrixSubtraction(double[][] MatrixA, double[][] MatrixB){
		  int n = MatrixA.length;
		  int m = MatrixB[0].length;
		  double[][] MatrixC = new double[n][m];
		  for(int i=0;i<n;i++){
			  for(int j=0;j<n;j++){
				  MatrixC[i][j] = MatrixA[i][j]-MatrixB[i][j];
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
	  public static double[][] matrixScalarMuli(double[][] Matrix, double scalar){
		  int n = Matrix.length;
		  for(int i=0;i<n;i++){
			  for(int j=0;j<n;j++){
				  Matrix[i][j] = Matrix[i][j]*scalar;
			  }
		  }
		  return Matrix;
	  }
	  
	  public static double[] findMidPoint(List<Double[]> points){
		  double[] mid = {0,0,0};
		  for(int j=0;j<points.size();j++){
			  mid=vectorAdd(makePrimative(points.get(j)),mid);
		  }
		  if(points.size()!=0){
		  mid = vectorScalarMult(mid, 1/points.size());
		  }
		  return mid;
	  }
	  
	  public static double distBetween(double[] pointA, double[] pointB){
		  return vectorMag(vectorSubtract(pointA,pointB));
	  }
	  
	  public static double distToEntity(double[] pointA, Entity target)
	  {
		  if(target!=null)
		  return vectorMag(vectorSubtract(pointA, new double[]{target.posX,target.posY,target.posZ}));
		  return 0;
	  }
	  
	  public static double[] vectorCopy(double[] vector){
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
	  public static double[] findNextSolidBlock(World worldObj,final double[] source,final double[] direction, double range){
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
	
	  public static double[] getVectorToEntity(double[] source, Entity target)
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
	  public static boolean isVisibleRange(World worldObj, double[] source, double[] direction, double range){
		  
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
	  
	  public static boolean isVisibleEntityFromLocation(World worldObj, Entity target, double[] location){
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
	  public static boolean isVisibleLocation(World worldObj, double[] source, double[] location){
		  
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
	  
    public static boolean isVisibleEntityFromEntity(Entity looker, Entity target, double rMax)
    {
    	double[] location = {looker.posX, looker.posY, looker.posZ};
    	return isVisibleEntityFromLocation(looker.worldObj, target, location);
    }
  

    //TODO make this not continue checking each pixel if the block or aabb was null.
	public static List<Entity> firstEntityOnLine(double range, double[] direction, double[] source, World worldObj, boolean effect)
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
	
	
	public static boolean isEntityVisiblefirstEntityOnLine(Entity target, double[] direction, double[] source, World worldObj)
	{
		direction = vectorNormalize(direction);
		List<Entity> targets = firstEntityOnLine(distToEntity(source, target), direction, source, worldObj, false);
		return target==null?false:targets==null?false:targets.contains(target);
	}
	  
	public static boolean isPointClearBlocks(double x, double y, double z, World worldObj)
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
	
	  
	public static boolean isPointClearOfaabb(double x, double y, double z, AxisAlignedBB aabb)
	{
		if(y<=aabb.maxY&&y>=aabb.minY)
			return false;
		if(z<=aabb.maxZ&&z>=aabb.minZ)
			return false;
		if(x<=aabb.maxX&&x>=aabb.minX)
			return false;
		
		return true;
	}
	  
}
