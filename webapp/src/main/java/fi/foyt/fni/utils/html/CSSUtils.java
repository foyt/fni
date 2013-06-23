package fi.foyt.fni.utils.html;

import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.dom.CSSRuleListImpl;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.Property;

public class CSSUtils {

  public static CSSStyleSheet createStyleSheet() {
    CSSStyleSheet styleSheet = new CSSStyleSheetImpl();
    return styleSheet;
  }
  
  public static CSSStyleSheet addRule(CSSStyleSheet styleSheet, CSSRule rule) {
    CSSRuleListImpl rules = (CSSRuleListImpl) styleSheet.getCssRules();
    rules.add(rule);
    return styleSheet;
  }
  
  public static String getStyleSheetAsString(CSSStyleSheet styleSheet) {
    CSSRuleList cssRuleList = styleSheet.getCssRules();

    StringBuilder result = new StringBuilder();
    for (int i = 0, l = cssRuleList.getLength(); i < l; i++) {
      CSSRule cssRule = cssRuleList.item(i);
      result.append(cssRule.getCssText());
      if (i < (l - 1)) {
        result.append(' ');
      }
    }

    return result.toString();
  }

  public static CSSStyleRule createStyleRule(String selectorText) {
    CSSStyleRuleImpl rule = new CSSStyleRuleImpl();
    rule.setSelectorText(selectorText);
        
    return rule;
  }
  
  public static Property addPropery(CSSStyleRule cssRule, String propertyName, String value) {
    return addPropery(cssRule, propertyName, value, false);
  }
   
  public static Property addPropery(CSSStyleRule cssRule, String propertyName, String value, boolean important) {
    CSSValueImpl cssValue = new CSSValueImpl();
    cssValue.setCssText(value);
    Property property = new Property(propertyName, cssValue, important);
    CSSStyleDeclarationImpl styleDeclaration = (CSSStyleDeclarationImpl) cssRule.getStyle();
    if (styleDeclaration == null) {
      styleDeclaration = new CSSStyleDeclarationImpl();
      ((CSSStyleRuleImpl) cssRule).setStyle(styleDeclaration);
    } 
    
    styleDeclaration.addProperty(property);
    
    return property;
  }

}
