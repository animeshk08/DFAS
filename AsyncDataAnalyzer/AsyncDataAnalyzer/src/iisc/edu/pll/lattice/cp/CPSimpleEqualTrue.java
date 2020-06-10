package iisc.edu.pll.lattice.cp;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.expressions.Constant;
import iisc.edu.pll.expressions.ExpressionComponent;
import iisc.edu.pll.expressions.Variable;

public class CPSimpleEqualTrue extends CPFunction {

	// can only handle true branches of equalities of type var1 == var2 or var1
	// == const
	String lhs;
	
	ExpressionComponent rhs;

	public CPSimpleEqualTrue(ArrayList<String> args) {
		if (args.size() < 2) {
			lhs = "";
			rhs = new Constant(0);
			return;
		}

		lhs = args.get(0);
		String[] rhsTerm = args.get(1).split(",");

		String term = rhsTerm[1];
		String coeff = rhsTerm[0];
		if (StringUtils.isNumeric(term))
			rhs = new Constant(Integer.parseInt(term) * Integer.parseInt(coeff));
		else
			rhs = new Variable(Integer.parseInt(coeff), term);
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		ConstantPropagation val = (ConstantPropagation) v;

		
		if (val.isBot())
			return Globals.botVal;

		ConstantPropagation newVal = new ConstantPropagation(val);

		ConstantPropagationSingle varVal = val.values[Globals.varNum.get(lhs)];
		if (varVal.isBot()) {
			newVal = (ConstantPropagation) Globals.botVal;
		} else if (varVal.isTop()) {
			if (rhs instanceof Constant) {
				newVal.values[Globals.varNum.get(lhs)] = new ConstantPropagationSingle(((Constant) rhs).getValue(),
						false, false);
				//newVal.values[Globals.varNum.get(lhs)] = new ConstantPropagationSingle(0,false, true);
				
			}
			if (rhs instanceof Variable) {
				Variable rhsVar = (Variable) rhs;
				ConstantPropagationSingle rhsVal = val.values[Globals.varNum.get(rhsVar.getVar())];
				if (rhsVal.isTop())
					newVal.values[Globals.varNum.get(lhs)] = new ConstantPropagationSingle(0, false, true);
				else if (rhsVal.isBot())
					newVal = (ConstantPropagation) Globals.botVal;
				else { //set lhs to the constant value of the rhs
					int finalVal = rhsVar.getCoeff() * rhsVal.getValue();
					newVal.values[Globals.varNum.get(lhs)] = new ConstantPropagationSingle(finalVal, false, false);
					//newVal.values[Globals.varNum.get(lhs)] = new ConstantPropagationSingle(0, false, true);
				}

			}

		} else { // lhs is a constant
			if (rhs instanceof Constant) {
				int finalVal = ((Constant) rhs).getValue();
				int lhsVal = varVal.getValue();
				if (lhsVal == finalVal)
					newVal.values[Globals.varNum.get(lhs)] = new ConstantPropagationSingle(finalVal, false, false);
				else
					newVal = (ConstantPropagation) Globals.botVal;
			}
			if (rhs instanceof Variable) {
				Variable rhsVar = (Variable) rhs;
				ConstantPropagationSingle rhsVal = val.values[Globals.varNum.get(rhsVar.getVar())];
				 if (rhsVal.isBot())
					newVal= (ConstantPropagation) Globals.botVal; 					
				else if( rhsVal.isTop())
				{ //set rhs equal to lhs
					newVal.values[Globals.varNum.get(rhsVar.getVar())] = new ConstantPropagationSingle(varVal.getValue(), false, false);
					//newVal.values[Globals.varNum.get(rhsVar.getVar())] = new ConstantPropagationSingle(0, false, true);
				}
				else {
					int finalVal = rhsVar.getCoeff() * rhsVal.getValue();
					int lhsVal = varVal.getValue();
					if (lhsVal == finalVal)
						newVal.values[Globals.varNum.get(lhs)] = new ConstantPropagationSingle(finalVal, false, false);
					else
						newVal= (ConstantPropagation) Globals.botVal; //return bot

				}

			}
			
		}

		return newVal;
	}

	
	@Override
	public String toString()
	{
		return "CPSimpleEqualTrueFunction [lhs=" + lhs + ", rhs=" + rhs + "]";
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}
}