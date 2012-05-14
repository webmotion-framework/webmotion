/*
 * #%L
 * WebMotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.debux.webmotion.server.parser;

import org.debux.webmotion.server.mapping.Mapping;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

/**
 * Parboile parser for mapping file. The method to read the mapping is <code>mapping</code>.
 * For more information on create mapping @see MappingParser.
 * 
 * @author julien
 */
@BuildParseTree
public class ParboiledMappingParser extends BaseParser {
    
    protected Mapping mapping;
    
    // First parse mapping
    public Rule mapping() {
        mapping = new Mapping();
        return Sequence(sections(), EOI);
    }
    
    public Rule sections() {
        return ZeroOrMore(
                    FirstOf(
                        comment(),
                        sectionConfig(),
                        sectionErrors(),
                        sectionFilters(),
                        sectionActions(),
                        sectionExtensions(),
                        sectionProperties()
                    )
                );
    }
    
    @SuppressNode
    public Rule comment() {
        return Sequence("#", End(), NewLine());
    }
    
    // Section properties
    public Rule sectionProperties() {
        return Sequence(propertiesSection(), ZeroOrMore(FirstOf(comment(), propertiesRule())));
    }
    
    public Rule propertiesSection() {
        return Sequence('[', propertiesIdentifier(), ".properties]", NewLine());
    }
    
    @SuppressSubnodes
    public Rule propertiesIdentifier() {
        return OneOrMore(LetterProperties());
    }
    
    public Rule propertiesRule() {
        return Sequence(
                    propertiesName(),
                    "=",
                    propertiesValue(),
                    NewLine()
            );
    }
    
    @SuppressSubnodes
    public Rule propertiesName() {
        return ZeroOrMore(
                    FirstOf(
                        fromStringLiteral("\\="),
                        Sequence(TestNot(FirstOf("=", NewLine())), ANY)
                    )
                );
    }
    
    @SuppressSubnodes
    public Rule propertiesValue() {
        return ZeroOrMore(
                    FirstOf(
                        Sequence("\\", NewLine()),
                        Sequence(TestNot(NewLine()), ANY)
                    )
                );
    }
    
    // Section config
    public Rule sectionConfig() {
        return Sequence(configSection(), ZeroOrMore(FirstOf(comment(), configRule())));
    }
    
    public Rule configSection() {
        return Sequence("[config]", NewLine());
    }
    
    public Rule configRule() {
        return Sequence(
                    configName(),
                    Optional(WhiteSpace()),
                    "=",
                    Optional(WhiteSpace()),
                    configValue(),
                    NewLine()
            );
    }
    
    @SuppressSubnodes
    public Rule configName() {
        return FirstOf(
                "package.views",
                "package.base",
                "package.filters",
                "package.actions",
                "package.errors",
                "javac.debug",
                "server.async",
                "server.encoding",
                "server.error.page",
                "server.controller.scope",
                "server.listener.class",
                "server.main.handler.class",
                "server.secret",
                "server.static.autodetect");
    }
    
    @SuppressSubnodes
    public Rule configValue() {
        return End();
    }
    
    // Section error
    public Rule sectionErrors() {
        return Sequence(errorSection(), ZeroOrMore(FirstOf(comment(), errorRule())));
    }
    
    public Rule errorSection() {
        return Sequence("[errors]", NewLine());
    }
    
    public Rule errorRule() {
        return Sequence(
                    FirstOf("*", errorCode(), errorException()),
                    WhiteSpace(),
                    FirstOf(
                        errorView(),
                        errorRedirect(),
                        errorActionUrl(),
                        errorJava()
                    ),
                    NewLine()
            );
    }    

    public Rule errorView() {
        return Sequence("view:", errorViewValue());
    }    
    
    public Rule errorViewValue() {
        return End();
    }    
    
    public Rule errorRedirect() {
        return Sequence(FirstOf("url:", "redirect"), errorUrlValue());
    }    
    
    public Rule errorActionUrl() {
        return Sequence("forward:", errorUrlValue());
    }    
    
    public Rule errorUrlValue() {
        return End();
    }    
    
    public Rule errorJava() {
        return Sequence(Optional("action:"), errorJavaValue());
    }
        
    public Rule errorJavaValue() {
        return QualifiedIdentifier();
    }
    
    @SuppressSubnodes
    public Rule errorCode() {
        return Sequence("code:", Digit(), Digit(), Digit());
    }
    
    @SuppressSubnodes
    public Rule errorException() {
        return QualifiedIdentifier();
    }
    
    // Section filter
    public Rule sectionFilters() {
        return Sequence(filterSection(), ZeroOrMore(FirstOf(comment(), filterRule())));
    }
    
    public Rule filterSection() {
        return Sequence("[filters]", NewLine());
    }
    
    public Rule filterRule() {
        return Sequence(Method(),
                    WhiteSpace(),
                    filterPath(),
                    WhiteSpace(),
                    filterJava(),
                    Optional(WhiteSpace(), filterParameters()),
                    NewLine()
            );
    }
    
    public Rule filterJava() {
        return Sequence(Optional("action:"), filterJavaValue());
    }
    
    public Rule filterJavaValue() {
        return QualifiedIdentifier();
    }
    
    @SuppressSubnodes
    public Rule filterPath() {
        return OneOrMore(
                    FirstOf(
                        fromStringLiteral("/*"),
                        Sequence("/", ZeroOrMore(LetterPath()))
                    )
                );
    }
    
    public Rule filterParameters() {
        return Sequence(filterParameter(), ZeroOrMore(",", filterParameter()));
    }
    
    public Rule filterParameter() {
        return Sequence(filterName(),
                        "=",
                        filterValue()
                );
    }
    
    @SuppressSubnodes
    public Rule filterName() {
        return OneOrMore(LetterParameter());
    }
    
    @SuppressSubnodes
    public Rule filterValue() {
        return ZeroOrMore(LetterParameterValue());
    }
    
    // Section extension
    public Rule sectionExtensions() {
        return Sequence(extensionSection(), ZeroOrMore(FirstOf(comment(), extensionRule())));
    }
    
    public Rule extensionSection() {
        return Sequence("[extensions]", NewLine());
    }
    
    public Rule extensionRule() {
        return Sequence(
                    extensionPath(),
                    WhiteSpace(),
                    extensionFile(),
                    NewLine()
            );
    }
    
    @SuppressSubnodes
    public Rule extensionPath() {
        return OneOrMore(Sequence("/", ZeroOrMore(LetterPath())));
    }
    
    @SuppressSubnodes
    public Rule extensionFile() {
        return End();
    }
    
    // Section action
    public Rule sectionActions() {
        return Sequence(actionSection(), ZeroOrMore(FirstOf(comment(), actionRule())));
    }
    
    public Rule actionSection() {
        return Sequence("[actions]", NewLine());
    }    
    
    public Rule actionRule() {
        return Sequence(
                    Method(),
                    WhiteSpace(),
                    actionPath(),
                    Optional("?", actionParameters()),
                    WhiteSpace(),
                    FirstOf(actionView(), actionRedirect(), actionForward(), actionJava()),
                    Optional(WhiteSpace(), actionDefaultParameters()),
                    NewLine()
            );
    }
        
    public Rule actionVariable() {
        return Sequence("{", Optional(QualifiedIdentifier()), Optional(":", actionPattern()), "}");
    }
    
    @SuppressSubnodes
    public Rule actionPattern() {
        return ZeroOrMore(
            FirstOf(
                fromStringLiteral("\\{"),
                fromStringLiteral("\\}"),
                Sequence(TestNot(FirstOf("{", "}", NewLine())), ANY)
            )
        );
    }
    
    public Rule actionPath() {
        return OneOrMore(
            FirstOf(
                Sequence(actionPathSlash(), actionVariable()),
                Sequence(actionPathSlash(), actionPathStatic()),
                actionPathSlash()
            )
        );
    }

    public Rule actionPathSlash() {
        return fromStringLiteral("/");
    }

    @SuppressSubnodes
    public Rule actionPathStatic() {
        return OneOrMore(LetterPath());
    }
    
    public Rule actionParameters() {
        return Sequence(actionParameter(), ZeroOrMore("&", actionParameter()));
    }
    
    public Rule actionParameter() {
        return Sequence(
                actionName(),
                Optional("=", Optional(FirstOf(actionVariable(), actionValue())))
            );
    }
    
    @SuppressSubnodes
    public Rule actionName() {
        return OneOrMore(LetterParameter());
    }
    
    @SuppressSubnodes
    public Rule actionValue() {
        return OneOrMore(LetterParameterValue());
    }
    
    @SuppressSubnodes
    public Rule actionSimpleVariable() {
        return Sequence("{", QualifiedIdentifier(), "}");
    }
    
    public Rule actionView() {
        return Sequence(
                "view:", 
                actionViewValue()
            );
    }
    
    @SuppressSubnodes
    public Rule actionViewValue() {
        return ZeroOrMore(
                FirstOf(
                    actionSimpleVariable(),
                    fromStringLiteral("\\{"),
                    fromStringLiteral("\\}"),
                    Sequence(TestNot(FirstOf("{", "}", NewLine())), ANY)
                )
            );
    }
    
    public Rule actionRedirect() {
        return Sequence(
                FirstOf("url:", "redirect"),
                actionRedirectValue()
            );
    }
    
    @SuppressSubnodes
    public Rule actionRedirectValue() {
        return ZeroOrMore(
                FirstOf(
                    actionSimpleVariable(),
                    Sequence(TestNot(FirstOf("{", "}", NewLine())), ANY)
                )
        );
    }
    
    public Rule actionForward() {
        return Sequence(
                "forward:", 
                actionForwardValue()
            );
    }
    
    @SuppressSubnodes
    public Rule actionForwardValue() {
        return ZeroOrMore(
                FirstOf(
                    actionSimpleVariable(),
                    Sequence(TestNot(FirstOf("{", "}", NewLine())), ANY)
                )
        );
    }
    
    public Rule actionJava() {
        return Sequence(
                Optional(actionJavaType()),
                actionJavaValue()
            );
    }
    
    @SuppressSubnodes
    public Rule actionJavaType() {
        return FirstOf("action:", "sync:", "async:");
    }
    
    @SuppressSubnodes
    public Rule actionJavaValue() {
        return OneOrMore(Sequence(actionIdentifier(), ZeroOrMore(".", actionIdentifier())));
    }
    
    public Rule actionIdentifier() {
        return OneOrMore(
                FirstOf(
                    actionSimpleVariable(),
                    Sequence(Letter(), ZeroOrMore(LetterOrDigit()))
                )
            );
    }
    
    public Rule actionDefaultParameters() {
        return Sequence(actionDefaultParameter(), ZeroOrMore(",", actionDefaultParameter()));
    }
    
    public Rule actionDefaultParameter() {
        return Sequence(actionDefaultName(),
                        "=",
                        actionDefaultValue()
                );
    }
    
    @SuppressSubnodes
    public Rule actionDefaultName() {
        return OneOrMore(LetterParameter());
    }
    
    @SuppressSubnodes
    public Rule actionDefaultValue() {
        return ZeroOrMore(LetterParameterValue());
    }
    
    // Common
    public Rule Method() {
        return Sequence(MethodValue(), ZeroOrMore(",", MethodValue()));
    }
    
    public Rule MethodValue() {
        return FirstOf(
                "*",
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "HEAD");
    }

    @SuppressNode
    public Rule NewLine() {
        return FirstOf(OneOrMore(FirstOf("\r\n", '\r', '\n')), EOI);
    }
    
    @SuppressNode
    public Rule WhiteSpace() {
        return OneOrMore(AnyOf(" \t\r\n\f"));
    }
    
    @SuppressSubnodes
    protected Rule QualifiedIdentifier() {
        return Sequence(Identifier(), ZeroOrMore(".", Identifier()));
    }
    
    @SuppressSubnodes
    public Rule End() {
        return ZeroOrMore(Sequence(TestNot(NewLine()), ANY));
    }
    
    @SuppressSubnodes
    @MemoMismatches
    protected Rule Identifier() {
        return Sequence(Letter(), ZeroOrMore(LetterOrDigit()));
    }
    
    protected Rule LetterPath() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$', '-', '.');
    }

    protected Rule LetterParameter() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$', '-', '.');
    }

    protected Rule LetterProperties() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$', '-');
    }

    protected Rule LetterParameterValue() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$', '-', '%', '.', "/");
    }

    protected Rule Letter() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '_', '$');
    }

    @MemoMismatches
    protected Rule LetterOrDigit() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$');
    }
    
    protected Rule Digit() {
        return CharRange('0', '9');
    }
    
}
