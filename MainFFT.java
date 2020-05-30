import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;



public class MainFFT {
	
	static double []vectorX;
	static double [] h1;
	static double [] h2;
	static int nmbr;
	public static void main(String[] args) throws InterruptedException {
		for(nmbr = 64; nmbr< 2048;nmbr *=2) {
			vectorX = generate(10, nmbr);
			double MX = findM(vectorX);
			double D = findD(vectorX, MX);
			
			long start = System.nanoTime();
			fourierF(vectorX, nmbr);
			long finish = System.nanoTime();
			long timeElapsed = finish - start;
			System.out.println("Elapsed time for fast(n="+nmbr +"): "+timeElapsed);
			
			long startH = System.nanoTime();
			Thrd1 ts1 = new Thrd1();
			Thrd2 ts2 = new Thrd2();
			Thread t1  = new Thread(ts1);
			Thread t2 = new Thread(ts2);
			t1.start();
			t2.start();
			t2.join();
			t1.join();
			plusH(h1,h2);
			long finishH = System.nanoTime();
			long timeElapsedH = finishH - startH;
			System.out.println("Elapsed time for half(n="+nmbr +"): "+timeElapsedH);
		}
		
		
		
	}
	public static class Thrd1 implements Runnable {
		@Override
		public void run() {
			 h1 =fourierH(vectorX, nmbr,0);
		}
		}
	
	public static class Thrd2 implements Runnable {
		@Override
		public void run() {
			 h2 =fourierH(vectorX, nmbr,nmbr/2);
		}
		}
	static double[] w=new double[nmbr];
	static double[] wi=new double[nmbr];
	static double omega = 900;
	static double omega_stp= omega/10;
	
	public static double[] plusH(double [] h1,double [] h2) {
		for(int i = 0;i<h1.length;i++) {
			h1[i] += h2[i];
		}
		return h1;
	}
	
	public static double makeA() {
		double a = Math.random();
		return a;
	}
	
	public static double makeFi() {
		double fi = Math.random();
		return fi;
	}
	
	public static double findX(double omg, double t) {
		double x = 0;
			double a = makeA();
			double fi = makeFi();
			x += a*Math.sin(omg*t+fi);
		
		return x;
	}
	
public static double[]/*[]*/ generate(int n, int N) {
		
		double [] res = new double [N];
		for(int t = 0; t< N;t++){
			double rs = 0;
			for(int i=0;i<n;i++) {
				rs+=(findX(omega_stp*i,t));
			}
			res[t]=rs;
		}
		return res;
	} 
	
	
	public static double findM(double []toMean) {
	 	double res = 0;
		for(int i = 0; i< toMean.length;i++){
				res += toMean[i];
		}
		res = res/(toMean.length*toMean.length);
		return res;
	}
	
	public static double findD(double []toDev,double mean) {
		double dev = 0;
		for(int i = 0; i< toDev.length;i++){
				dev += Math.pow(toDev[i]-mean, 2);
		}
		dev = /*Math.sqrt(*/dev/(toDev.length-1);//);
		return dev;
	}
	
	
	public static double[] autocorr(double [] check, int n, double mean) {
		double[] acorr = new double [n/2];
		for(int tau = 0; tau<n/2;tau++ ) {
			for(int t = 0; t<n-tau;t++) {
				acorr[tau] += (check[t]-mean)*(check[t+tau]-mean);
			}
			acorr[tau] /=(n-1);
		}
		System.out.println("acorr 0 is :"+acorr[0]);
		return acorr;
	}
	
	public static double[] corr(double [] checkX, double [] checkY, int n, double meanX, double meanY) {
		double[] acorr = new double [n/2];
		for(int tau = 0; tau<n/2;tau++ ) {
			for(int t = 0; t<n-tau;t++) {
				acorr[tau] += (checkX[t]-meanX)*(checkY[t+tau]-meanY);
			}
			acorr[tau] /=(n-1);
		}
		System.out.println("acorr 0 is :"+acorr[0]);
		return acorr;
	}
	
	public static void print(double []oneD) {
		for(double one : oneD) {
			System.out.println("x is "+one);
		}
	}
	
	public static double[] fourierH(double []x,double N,int half) {
		double[] f = new double[(int)N];
		double[] fi = new double[(int)N];
		for(int p = half;p<((N/2)+half);p++) {
			//x = generate(10,(int)N);
			for(int k = 0;k<N;k++) {
				
					f[p] +=x[k]*Math.cos(((2*Math.PI)/N)*(p)*k);
					fi[p] +=x[k]*Math.sin(((2*Math.PI)/N)*(p)*k);
			}
			f[p]=Math.sqrt(Math.pow(f[p],2)+Math.pow(fi[p],2));
		}
		return f;
	}

	public static double[] fourierF(double []x,double N) {
		double[]res = new double[(int)N];
		double[] f = new double[(int)N];
		double[] fi = new double[(int)N];
		double[] f2 = new double[(int)N];
		double[] fi2 = new double[(int)N];
		for(int p = 0;p<N;p++) {
			for(int k = 0;k<N/2;k++) {
				
					f[p] +=x[2*k]*Math.cos(((2*Math.PI)/(N/2))*p*k);
					f2[p]+=x[2*k+1]*Math.cos(((2*Math.PI)/(N))*p*(2*k+1));
					fi[p] +=x[2*k]*Math.sin(((2*Math.PI)/(N/2))*p*k);
					fi2[p]+=x[2*k+1]*Math.sin(((2*Math.PI)/(N))*p*(2*k+1));
					
			}
			f[p]=Math.sqrt(Math.pow(f[p],2)+Math.pow(fi[p],2));
			f2[p]=Math.sqrt(Math.pow(f2[p],2)+Math.pow(fi2[p],2));
			res[p]=f[p]+f2[p];
		}
		return res;
	}

	public static void makePlot(double [] forPlot,String name) {
		double []N = new double[forPlot.length];
		for(int i=0;i<256;i++) {
			N[i]=i;
		}
		XYChart chart = QuickChart.getChart("Lab3 "+name, "p", "F(p)", "F(p)", N, forPlot);
		new SwingWrapper(chart).displayChart();
	}
}
