package core.store;

import java.util.Comparator;

public class SetItemComparator implements Comparator<SetItem> {

	@Override
	public int compare(SetItem arg0, SetItem arg1) {
		double res=arg0.getScore()-arg1.getScore();
		if(res>0)return 1;
		else if (res<0) return -1;
		else return arg0.getMember().compareTo(arg1.getMember());
	}

}
