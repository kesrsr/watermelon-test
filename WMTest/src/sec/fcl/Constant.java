package sec.fcl;

import android.util.Log;
import libsvm.svm_node;

public class Constant {
	public static void print(float[] v){
		System.out.println();
		for(int i = 0; i < v.length; i++)
			System.out.print(v[i] +" ");
		System.out.println();
	}
	

	public static void print(double[] v){
		System.out.println();
		for(int i = 0; i < v.length; i++)
			System.out.print(v[i] +" ");
		System.out.println();
	}
	
	public static void print(String[] v){
		System.out.println();
		for(int i = 0; i < v.length; i++)
			Log.e("WM", (v[i] +" "));
		System.out.println();
	}
	
	public static void print(svm_node[] v){
		System.out.println();
		for(int i = 0; i < v.length; i++)
			System.out.print(v[i].index +" ");
		System.out.println();
	}
}
