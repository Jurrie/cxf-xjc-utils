package org.apache.cxf.xjc.reproducable_builds;

import java.io.StringWriter;
import java.util.Map.Entry;

import javax.annotation.Generated;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.ErrorHandler;

public class ReproducableBuildPlugin extends com.sun.tools.xjc.addon.at_generated.PluginImpl
{
	// TODO: What about the // on the header?

	@Override
	public String getOptionName()
	{
		return "Xmark-generated-without-timestamp";
	}

	@Override
	public String getUsage()
	{
		return "  -Xmark-generated-without-timestamp: mark the generated code as @javax.annotation.Generated, without the 'date' parameter";
	}

	@Override
	public boolean run(final Outline outline, final Options opt, final ErrorHandler errorHandler)
	{
		final boolean result = super.run(outline, opt, errorHandler);
		if (!result)
		{
			return false;
		}

		for (ClassOutline classOutline : outline.getClasses())
		{
			processClass(classOutline);
		}

		return true;
	}

	private void processClass(final ClassOutline clazz)
	{
		for (JAnnotationUse annotation : clazz.implClass.annotations())
		{
			if (!annotation.getAnnotationClass().fullName().equals(Generated.class.getCanonicalName()))
			{
				continue;
			}

			clazz.implClass.removeAnnotation(annotation);
			final JAnnotationUse newAnnotation = clazz.implClass.annotate(Generated.class);
			duplicateAnnotationWithoutDate(annotation, newAnnotation);
			break;
		}

		for (Entry<String, JFieldVar> entry : clazz.implClass.fields().entrySet())
		{
			final JFieldVar field = entry.getValue();
			for (JAnnotationUse annotation : field.annotations())
			{
				if (!annotation.getAnnotationClass().fullName().equals(Generated.class.getCanonicalName()))
				{
					continue;
				}

				field.removeAnnotation(annotation);
				final JAnnotationUse newAnnotation = field.annotate(Generated.class);
				duplicateAnnotationWithoutDate(annotation, newAnnotation);
				break;
			}
		}

		for (JMethod method : clazz.implClass.methods())
		{
			for (JAnnotationUse annotation : method.annotations())
			{
				if (!annotation.getAnnotationClass().fullName().equals(Generated.class.getCanonicalName()))
				{
					continue;
				}

				method.removeAnnotation(annotation);
				final JAnnotationUse newAnnotation = method.annotate(Generated.class);
				duplicateAnnotationWithoutDate(annotation, newAnnotation);
				break;
			}
		}
	}

	private void duplicateAnnotationWithoutDate(final JAnnotationUse oldGeneratedAnnotation, final JAnnotationUse newGeneratedAnnotation)
	{
		for (Entry<String, JAnnotationValue> oldAnnotationMember : oldGeneratedAnnotation.getAnnotationMembers().entrySet())
		{
			if (oldAnnotationMember.getKey().equals("date"))
			{
				continue;
			}

			final JAnnotationValue oldValue = oldAnnotationMember.getValue();
			if (oldValue instanceof JAnnotationArrayMember || oldValue instanceof JAnnotationUse)
			{
				// Euh?
			}
			else
			{
				final StringWriter sw = new StringWriter();
				final JFormatter f = new JFormatter(sw);
				oldValue.generate(f);
				String newValue = StringEscapeUtils.unescapeJava(sw.getBuffer().toString());
				if (!newValue.isEmpty() && newValue.charAt(0) == '"')
				{
					// This is a String value, we should remove the leading and trailing double quote
					newValue = newValue.substring(1, newValue.length() - 2);
				}
				newGeneratedAnnotation.param(oldAnnotationMember.getKey(), newValue);
			}
		}
	}
}