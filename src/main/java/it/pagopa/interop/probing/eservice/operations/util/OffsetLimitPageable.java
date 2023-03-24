package it.pagopa.interop.probing.eservice.operations.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Builder;

@Builder
public class OffsetLimitPageable implements Pageable {

	private Integer limit;
	private Integer offset;
	private Sort sort;

	@Override
	public int getPageNumber() {
		return limit;
	}

	@Override
	public int getPageSize() {
		return limit;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public Pageable next() {
		return null;
	}

	@Override
	public Pageable previousOrFirst() {
		return null;
	}

	@Override
	public Pageable first() {
		return null;
	}

	@Override
	public Pageable withPage(int pageNumber) {
		return null;
	}

	@Override
	public boolean hasPrevious() {
		return false;
	}

}
