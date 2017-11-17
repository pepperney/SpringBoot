package com.pepper.boot.security;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.boot.consts.SystemCode;

public interface Checker {
	
	String USERDETAILS_KEY = "_USER_DETAILS";

	SystemCode check(HttpServletRequest request, HttpServletResponse response);

	public class CheckerChain implements Checker {

		private static final Logger log = LoggerFactory.getLogger(CheckerChain.class);

		private Checker checker;
		private CheckerChain chain;

		public CheckerChain(Iterator<Checker> iterator) {
			this.checker = iterator.next();
			if (iterator.hasNext()) {
				this.chain = new CheckerChain(iterator);
			}
		}

		@Override
		public SystemCode check(HttpServletRequest request, HttpServletResponse response) {
			SystemCode result = checker.check(request, response);
			if (SystemCode.SYSTEM_SUCCESS.equals(result) && chain != null) {
				log.trace("------> {} is checking... ...", checker.getClass().getName());
				return chain.check(request, response);
			} else {
				return result;
			}
		}
	}

}
