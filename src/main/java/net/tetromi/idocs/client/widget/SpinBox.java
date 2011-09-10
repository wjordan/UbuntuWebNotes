package net.tetromi.idocs.client.widget;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;

/** Simple integer spinbox widget. */
public class SpinBox extends Composite {
    private final Button up = new Button("↑");
    private final Button down = new Button("↓");
    private final Label number = new Label();
    private int value;
    private final int min;
    private final int max;

    private final ArrayList<Listener> listeners = new ArrayList<Listener>();

    public SpinBox(final int min, final int max, final int step, int start) {
        if(min > max || min > start || max < start) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        this.min = min;
        this.max = max;
        this.value = start;
        number.setText(Integer.toString(start));
        number.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        number.getElement().getStyle().setBorderWidth(2, Style.Unit.PT);
        VerticalPanel v = new VerticalPanel();
        v.add(up);
        v.add(number);
        v.add(down);
        number.setWidth("100%");
        up.setWidth("100%");
        down.setWidth("100%");
        up.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                if (value + step < max) value += step;
                else value = max;
                set();
            }
        });
        down.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                if (value - step < max) value -= step;
                else value = min;
                set();
            }
        });
        set();
        initWidget(v);
    }

    private void set() {
        up.setEnabled(value < max);
        down.setEnabled(value > min);
        number.setText(Integer.toString(value));
        for(Listener l: listeners) l.valueChanged(value);
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public static interface Listener {
        void valueChanged(int newValue);
    }
}
