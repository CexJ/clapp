package com.cexj.clapp.results;

import java.util.ArrayList;
import java.util.List;

import com.cexj.clapp.utils.either.Either;

public class PullResult<I> {

	private final Either<Exception, I> finalResult;
	private final List<Stage<?>> stages;
	
	private PullResult(Either<Exception, I> finalResult, List<Stage<?>> stages) {
		this.finalResult = finalResult;
		this.stages = stages;
	}

	public static <I> PullResult<I> of(Either<Exception, I> finalResult, List<Stage<?>> stages) {
		return new PullResult<>(finalResult, stages);
	}

	public PullResult<I> addStage(Stage<?> stage) {
		List<Stage<?>> newStages = new ArrayList<>(stages);
		newStages.add(stage);
		return new PullResult<>(finalResult, newStages);
	}
	
	public Either<Exception, I> getFinalResult() {
		return finalResult;
	}

	public List<Stage<?>> getStages() {
		return stages;
	}	
	
	
}
