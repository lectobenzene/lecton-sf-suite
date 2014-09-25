package com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.context.providers.labelprovider;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.FieldModel;
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.IndexedCollectionModel;
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.KeyedCollectionModel;
import com.tcs.mobility.sf.lecton.xml2xsd2java.Activator;

/**
 * This class is copied from the BTT plugin.
 * No scope of modification here.
 * 
 * @author Saravana
 *
 */
public class ContextParseLabelProvider extends LabelProvider implements IStyledLabelProvider {

	private Image image;
	private Display display;
	private Color colorDataType;

	public ContextParseLabelProvider(Display display) {
		this.display = display;
		colorDataType = new Color(display, new RGB(255, 0, 0));
	}

	@Override
	public Image getImage(Object element) {

		if (element instanceof KeyedCollectionModel) {
			image = Activator.getDefault().createImage("icons/kcoll.png");
		} else if (element instanceof IndexedCollectionModel) {
			image = Activator.getDefault().createImage("icons/icoll.png");
		} else if (element instanceof FieldModel) {
			image = Activator.getDefault().createImage("icons/field.png");
		} else {
			image = super.getImage(element);
		}
		return image;
	}

	@Override
	public void dispose() {
		colorDataType.dispose();
		colorDataType = null;
		super.dispose();
	}

	@Override
	public String getText(Object element) {
		return getStyledText(element).toString();
	}

	@Override
	public StyledString getStyledText(Object element) {
		String normalText = "ROOT";
		String dataType = null;
		if (element instanceof KeyedCollectionModel) {
			normalText = ((KeyedCollectionModel) element).getId();
		} else if (element instanceof IndexedCollectionModel) {
			normalText = ((IndexedCollectionModel) element).getId();
		} else if (element instanceof FieldModel) {
			normalText = ((FieldModel) element).getId() + " ";
			dataType = ((FieldModel) element).getDataType();
		}
		Styler styler = new Styler() {

			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = colorDataType;
			}
		};
		StyledString styledString = new StyledString(normalText);
		if (dataType != null) {
			styledString.append("(" + dataType + ")", styler);
		}
		return styledString;
	}

}
