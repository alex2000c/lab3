package bsu.rfe.java.group6.lab3.Dorozhko.varA6;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class GornerTableModel extends AbstractTableModel {
    private Double from, to, step;  
    private Double[] coefficients;// от, до и шаг
    public GornerTableModel(Double from, Double to, Double step,Double[] coefficients) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coefficients = coefficients;
    }

    public Double getFrom() {
        return from;                //возвращает т к у нас все эти переменные private
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }


    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        //Вычислить количество значений аргумента исходя из шага
        return new Double(Math.ceil((to-from)/step)).intValue()+1;
    }

    public Object getValueAt(int row, int col) {
        //Вычислить значение X (col=0) как НАЧАЛО_ОТРЕЗКА + ШАГ*НОМЕР_СТРОКИ
    	double x = from + step*row;
    	Double result = 0.0;
    	
		for ( int i = coefficients.length-1; i >= 0; i-- )
			result = coefficients[i]+result*x;
    	
    	if (col==0) 
		{
			// Если запрашивается значение 1-го столбца, то это X
			return x;
		} 
			if(col==1)
			{
				// Если запрашивается значение 2-го столбца, то это значение многочлена
				
			
				return result;	
			}
				else
					return (Math.abs(Math.round(result) - result) <= 0.1);
				
		/*
        double x = from + step*row;
        Double result = 0.0;
        
        for(int i = 0; i < coefficients.length; i++)
    		result += Math.pow(x,i)*coefficients[i];
       
        if(col == 0) 
           return x;
       
        else if (col == 1) 
        		
        	return result;
         else
        	 return (Math.abs(Math.round(result) - result) <= 0.1);
        	 */
        	
        	 
      }
        

    public String getColumnName(int col) {
        switch (col) {
            case 0: return "Значение х";
            case 1: return "значение многочлена";
        	default: return "близко к целому";
        }	
    }

    public Class<?> getColumnClass(int col) {
        //И в 1-ом и во 2-ом столбце находятся значения типа Double
    	switch (col){
    	case 0: return Double.class;
    	case 1: return Double.class;
    	default: return Boolean.class; }
    }
}
