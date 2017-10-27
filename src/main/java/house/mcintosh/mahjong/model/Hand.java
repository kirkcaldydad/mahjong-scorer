package house.mcintosh.mahjong.model;

import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * A number of sets, making up a hand.
 * 
 * TODO: Enhance to support limit hands that are not made up of sets.
 */

public class Hand implements Iterable<Group>
{
	private final ObservableList<Group> m_backingSets;
	private final SortedList<Group> m_sortedSets;
	
	protected Hand()
	{
		m_backingSets = FXCollections.observableArrayList();
		m_sortedSets = new SortedList<Group>(m_backingSets, new SetComparator());
	}
	
	public void add(Group set)
	{
		m_backingSets.add(set);
	}

	@Override
	public Iterator<Group> iterator()
	{
		return m_sortedSets.iterator();
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[\n");
		
		for (Group set : this)
		{
			sb.append("  ");
			sb.append(set);
			sb.append('\n');
		}
		
		sb.append(']');
		
		return sb.toString();
	}
}
