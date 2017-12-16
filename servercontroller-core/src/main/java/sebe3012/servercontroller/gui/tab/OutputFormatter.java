package sebe3012.servercontroller.gui.tab;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import javafx.concurrent.Task;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sebe3012 on 09.05.2017.
 * A util class to control the formatting of the output
 */
public class OutputFormatter implements Closeable {

	public static final String BRACKET_PATTERN = "[\\[\\]]";

	public static final Pattern PATTERN = Pattern.compile("(?<BRACKET>" + BRACKET_PATTERN + ")");

	private ExecutorService executor;
	private CodeArea area;

	public void start(CodeArea area) {
		this.area = area;
		this.executor = Executors.newSingleThreadExecutor();

		area.setParagraphGraphicFactory(LineNumberFactory.get(area));
		area.richChanges()
				.filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
				.successionEnds(Duration.ofMillis(100))
				.supplyTask(this::computeHighlightingAsync)
				.awaitLatest(area.richChanges())
				.filterMap(t -> {
					if (t.isSuccess()) {
						return Optional.of(t.get());
					} else {
						t.getFailure().printStackTrace();
						return Optional.empty();
					}
				})
				.subscribe(this::applyHighlighting);
	}

	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		String text = area.getText();
		Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return computeHighlighting(text);
			}
		};

		if (!executor.isShutdown()) {
			executor.execute(task);
		}


		return task;
	}

	@Override
	public void close() {
		executor.shutdown();
	}

	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		area.setStyleSpans(0, highlighting);
	}

	private StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;

		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = matcher.group("BRACKET") != null ? "bracket" : null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}

		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}

}
