package sebe3012.servercontroller.api.util;

import java.util.function.Predicate;

/**
 * Created by Sebe3012 on 18.01.2017.
 * This class contains some string predicates
 */
public class StringPredicates {
	public static final Predicate<String> DO_NOTHING = s -> true;
	public static final Predicate<String> DEFAULT_CHECK = s -> !(s == null || s.trim().isEmpty());
	public static final Predicate<String> IS_NUMERIC = s -> s != null && s.matches("\\d*");
}