package classifierbayesnaive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Classifier {
	static ArrayList<Integer> trainLabels = new ArrayList<Integer>();
	static ArrayList<Integer> testLabels = new ArrayList<Integer>();
	static ArrayList<Integer> predictedLabels = new ArrayList<Integer>();
	static int [][]trainFaces = new int[451][4200];
	static int [][]testFaces = new int[150][4200];
	static HashMap<Integer, double[]> countTable = new HashMap<Integer,double[]>();
	static double muggle = 0.0;
	static double wizard = 0.0;
	public static void main(String args[]){
		try{
			inputLabelHandle("facedatatrainlabels",trainLabels);
		}catch(Exception e){
		}

		try{
			inputFaceHandle("facedatatrain",trainFaces);
		}catch(Exception e){
		}

		populateHashTable();
		trainFaces = null;trainLabels=null;

		try{
			inputLabelHandle("facedatatestlabels",testLabels);
		}catch(Exception e){
		}

		try{
			inputFaceHandle("facedatatrain",testFaces);
		}catch(Exception e){
		}

		naivePrediction();
		calculateAccuracy();

	}
	public static void calculateAccuracy(){
		int positive = 0;
		int negative = 0;
		for(int i=0;i<predictedLabels.size();i++)
		{
			if(testLabels.get(i)==predictedLabels.get(i))
				positive++;
			else
				negative++;
		}
		double accuracy = ((double)positive / (positive+negative))*100;
		System.out.println("Accuracy of the classifier is : " + accuracy+"%");
  		System.out.println("It has correctly classified "+positive+" instances out of "+(positive+negative)+" instances" );
	}
	public static void naivePrediction(){
		double human = 1.0;
		double no_human = 1.0;
		for(int i=0;i<testFaces.length;i++)
		{
			for(int j=0;j<testFaces[i].length;j++)
			{
				if(testFaces[i][j]==1)
				{
					human *= countTable.get(j)[3];
					no_human *= countTable.get(j)[2];
				}else{
					human *= countTable.get(j)[1];
					no_human *= countTable.get(j)[0];
				}
			}
			if(human*muggle > wizard*no_human)
				predictedLabels.add(1);
			else
				predictedLabels.add(0);
		}
	}
	public static void populateHashTable(){
		for(int i = 0 ;i<4200;i++)
		{
			double temp[] = new double[4];
			countTable.put(i,temp);
		}
		for(int i=0;i<trainFaces[0].length;i++)
		{
			for(int j=0;j<trainFaces.length;j++)
			{
				if(trainFaces[j][i]==0 && trainLabels.get(j)==0)
					countTable.get(i)[0]++;
				else if(trainFaces[j][i]==0 && trainLabels.get(j)==1)
					countTable.get(i)[1]++;
				else if(trainFaces[j][i]==1 && trainLabels.get(j)==0)
					countTable.get(i)[2]++;
				else
					countTable.get(i)[3]++;
			}
		}
		for(int i=0;i<4200;i++)
		{
			double temp[] = countTable.get(i);
			countTable.get(i)[0] = temp[0]/(temp[0]+temp[2]);
			countTable.get(i)[1] = temp[1]/(temp[3]+temp[1]);
			countTable.get(i)[2] = 1 - countTable.get(i)[0];
			countTable.get(i)[3] = 1 - countTable.get(i)[1];
		}
		for(int i=0;i<trainLabels.size();i++)
		{
			if(trainLabels.get(i)==1)
				muggle++;
			else
				wizard++;
		}
		muggle = muggle/(muggle+wizard);
		wizard = 1 - muggle;
	}
	public static void inputLabelHandle(String filename,ArrayList<Integer> labels)throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line=null;
		while( (line=br.readLine()) != null) {
			labels.add(Integer.parseInt(line));
		} 
		br.close();
	}
	public static void inputFaceHandle(String filename,int[][] faces)throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line=null;
		int linesRead = 0;
		int row=-1;
		int column = 0;
		while((line=br.readLine()) != null) {
			if(linesRead%70==0)
			{
				row++;
				column = 0;
			}
			for(int j =0;j<line.length();j++){
				if(line.charAt(j)=='#')
					faces[row][column]=1;
				else
					faces[row][column]=0;
				column++;
			}
			linesRead++;
		}
		br.close();
	}
}
