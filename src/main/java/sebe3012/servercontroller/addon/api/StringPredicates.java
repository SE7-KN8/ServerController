package sebe3012.servercontroller.addon.api;

import java.util.function.Predicate;

/**
 * Created by Sebe3012 on 18.01.2017.
 * This class contains all String tests for {@link DialogRow}
 */
public class StringPredicates {

	public static final Predicate<String> DO_NOTHING = s -> true;
	public static final Predicate<String> DEFAULT_CHECK = s -> !(s == null || s.trim().isEmpty());


}
