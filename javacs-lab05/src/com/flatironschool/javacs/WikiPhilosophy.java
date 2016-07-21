package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {

	final static WikiFetcher wf = new WikiFetcher();
    final static List<String> visited = new ArrayList<String>();
    private static int parenCounter = 0;

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 *
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 *
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

        // some example code to get you started

		String destination = "https://en.wikipedia.org/wiki/Philosophy";
        String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        String url = source;
        int limit = 7;
        for (int i = 0; i < limit; i++) {
            if (visited.contains(url)) {
                // in a loop
                return;
            } else {
                visited.add(url);
            }
            Elements paragraphs = wf.fetchWikipedia(url);
            Element elt = findFirstLink(paragraphs);
            if (elt == null) {
                return;
            }

            System.out.println("**" + elt.text() + "**");
            url = elt.attr("abs:href");

            if (url.equals(destination)) {
                //found it
                break;
            }
        }
    }

    public static Element findFirstLink(Elements paragraphs) {
        for (Element e : paragraphs) {
            Element potentialLink = parseElements(e);
            if (potentialLink != null) {
                return potentialLink;
            }
        }
        return null;

    }

    public static Element parseElements(Node root) {
        Iterable<Node> wikiIter = new WikiNodeIterable(root);
        for (Node node : wikiIter) {
            if (node instanceof TextNode) {
                String text  = ((TextNode)node).text();
                for (char c : text.toCharArray()) {
                    if (c == '(') {
                        parenCounter++;
                    } else if (c == ')') {
                        parenCounter--;
                    }
                }
            } else if (node instanceof Element) {
                if (getValidLink((Element)node)) {
                    return (Element)node;
                }
            }
        }
        return null;
    }

    public static boolean getValidLink(Element link) {
        return link.tagName().equals("a")
            && checkItalics(link)
            && parenCounter == 0
            && !link.attr("href").startsWith("#")
            && !link.attr("href").startsWith("/wiki/Help:");
    }


    public static boolean checkItalics(Element elt) {
        for (Element e = elt; e != null; e = e.parent()) {
            if (e.tagName().equals("i") || e.tagName().equals("em")) {
                return false;
            }
        }
        return true;
    }

}