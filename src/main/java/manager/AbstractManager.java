package manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import lifecycle.LifeCycle;

/**
 * Manager骨架实现
 * 
 * @author skywalker
 *
 */
public abstract class AbstractManager<T> implements Manager<T> {

	protected List<T> candidates;
	protected final ExecutorService executor;
	private ChooseStrategy<T> chooseStrategy;
	private final int s;
	
	public AbstractManager(int s, ExecutorService executor) {
		this(s, executor, null);
	}
	
	public AbstractManager(int s, ExecutorService executor, ChooseStrategy<T> chooseStrategy) {
		if (s < 1) {
			throw new IllegalArgumentException("The candidates count cant't be less than 1.");
		}
		candidates = new ArrayList<T>(s);
		this.s = s;
		this.executor = executor;
		if (chooseStrategy != null) {
			this.chooseStrategy = chooseStrategy;
		} else {
			this.chooseStrategy = new DefaultChooseStrategy<T>();
		}
	}
	
	@Override
	public void start() {
		for (int i = 0; i < s; i++) {
			T candidate = newCandidate();
			candidates.add(candidate);
			if (candidate instanceof LifeCycle) {
				LifeCycle lifeCycle = (LifeCycle) candidate;
				lifeCycle.start();
			}
		}
		chooseStrategy.setCandidates(candidates);
	}
	
	/**
	 * 生成一个候选人
	 * @return
	 */
	protected abstract T newCandidate();
	
	@Override
	public T chooseOne(Object param) {
		return chooseStrategy.choose(param);
	}
	
}