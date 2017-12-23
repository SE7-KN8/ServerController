package sebe3012.servercontroller.api.util;

import sebe3012.servercontroller.api.server.BasicServer;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Created by Sebe3012 on 18.01.2017.
 * This class contains some string predicates
 */
public class StringPredicates {
	public static final Predicate<String> DO_NOTHING = s -> true;
	public static final Predicate<String> DEFAULT_CHECK = s -> !(s == null || s.trim().isEmpty());

	public static final BiPredicate<String, BasicServer> SERVER_DONE_CHECK = (s, server) -> s.matches(server.getDoneRegex());


}