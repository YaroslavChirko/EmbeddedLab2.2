import java.util.Random;

public class MainGenetic {
	public static void main(String[] args) {
		System.out.println("Started");
		int [] gen = Genetic(20,1,1,1,1);
		vOut(gen);
	}
	
	static void vOut(int[] toOut) {
		for(int i = 0;i<toOut.length;i++) {
			System.out.println(toOut[i]);
		}
	}
	
	   static int[] Genetic(int res,int a, int b,int c,int d){
		   Random rGen = new Random();
	        int [][]iPop = popGen(4,4,res);
	        int [][] oGen = new int[4][4];
	        int mrt = 0;
	        int [] done = {0,0,0,0};
	        int n = 0;
	        int best_delta = 100000000;
	        toploop:
	        while(n<500){
	        	int [] delta = new int [4];
		        double[]dtr = new double[4];
	            for(int i = 0;i<4;i++){
	                delta[i]=Math.abs(res - (a*iPop[i][0]+b*iPop[i][1]+c*iPop[i][2]+d*iPop[i][3]));
	                System.out.println("delta is "+delta[i]);
	                
	                if(delta[i]==0){
	                    done = iPop[i];
	                    break toploop;
	                }
	                if(delta[i]<best_delta) {
		            	best_delta = delta[i];
		            	done = iPop[i];
		            }
	                dtr[i]= 1/(double)delta[i];
	            }
	            double totalDiv = dtr[0]+dtr[1]+dtr[2]+dtr[3];
	            double fit = 0;
	            int fitI=0;
	            for(int i = 0; i<4;i++){
	                dtr[i] = dtr[i]/totalDiv;
	                if(dtr[i]>fit){
	                    fit = dtr[i];
	                    fitI = i;
	                }
	            }
	            
	           
	            
	            for(int cnt =0;cnt<4;cnt++) {
	                    double rnd = rGen.nextDouble();//Math.random();
	                    if(rnd<=dtr[0]){
	                        oGen[cnt] = iPop[0];
	                    }else if(rnd<=dtr[0]+dtr[1]){
	                        oGen[cnt] = iPop[1];
	                    }else if(rnd<=dtr[0]+dtr[1]+dtr[2]){
	                        oGen[cnt] = iPop[2];
	                    }else{
	                        oGen[cnt] = iPop[3];
	                    }
	            }
	             
	            iPop = crossOv(oGen,2,2);
	            double mutationR = rGen.nextDouble();//Math.random();
	            if(mutationR<=0.02) {
	            	mutate(iPop,res);
	            	mrt++;
	            }
	            n++;
	        }
	        System.out.println("n is :"+n);
	        System.out.println("mrt is :"+mrt);
	        return done;

	    }

	    static int[][] crossOv(int[][]toCross,int cNum1,int cNum2){
	        int tmp = 0;
	        for(int i = 0; i<cNum1;i++){
	            tmp = toCross[1][i];
	            toCross[1][i] = toCross[0][i];
	            toCross[0][i] = tmp;
	        }

	        for(int i = 0; i<cNum2;i++){

	            tmp = toCross[3][i];
	            toCross[3][i] = toCross[2][i];
	            toCross[2][i]=tmp;
	        }
	        return toCross;
	    }
	    
	    static int[][] popGen(int geneN,int popN,int y){
	    	Random rGen = new Random();
	        int [][] pop = new int[popN][geneN];
	        for(int i =0;i<popN;i++){
	            for(int j = 0; j<geneN;j++){
	                pop[i][j]=rGen.nextInt((y/2)+1);//(int) Math.round((Math.random()*(y/2)));
	            }
	            vOut(pop[i]);
	        }
	        
	        return pop;
	    }
	    
	    static void mutate(int[][] mut,int maxBound) {
	    	Random rGen = new Random();
	    	int i = rGen.nextInt(4);//(int) (Math.random()*4);
	    	int j =  rGen.nextInt(4);//(int) (Math.random()*4);
	    	if(mut[i][j]!=maxBound/2) {
	    		mut[i][j]++;
	    	}else {
	    		mut[i][j]=0;
	    	}
	    	//System.out.println("mutated ["+i+"]["+j+"]");
	    }
}
