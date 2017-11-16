package org.eisenwave.vv.ui.cmd;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsed representation of a command call with both index and keyword parameters.
 */
public class CommandCall {
    
    private final static Pattern REGEX_PARAM = Pattern.compile("^-+");
    
    private final List<String> args = new ArrayList<>();
    private final Map<String, String> kwargs = new HashMap<>();
    
    private final Set<String> validKwArgs = new HashSet<>();
    
    private boolean strictOrder = false;
    private boolean strictParams = false;
    
    public CommandCall parse(String[] args, int offset) {
        if (offset < 0)
            throw new IllegalArgumentException("offset must be positive");
        if (args.length < offset)
            throw new IllegalArgumentException("no args given");
        
        boolean kwargsStart = false;
        String key = null;
        
        for (int i = offset; i < args.length; i++) {
            String arg = args[i];
            Matcher matcher = REGEX_PARAM.matcher(arg);
            
            if (matcher.find()) {
                kwargsStart = true;
                if (key != null)
                    this.kwargs.put(key, null);
                
                key = arg.substring(matcher.end());
                if (strictParams && !acceptsKeyword(key))
                    throw new IllegalArgumentException("invalid keyword argument \""+key+"\"");
            }
            
            else {
                if (key == null) { // indexed argument
                    if (kwargsStart && strictOrder)
                        throw new IllegalArgumentException("index and keyword args used out of order");
                    else
                        this.args.add(arg);
                }
                
                else { // keyword argument
                    kwargs.put(key, arg);
                    key = null;
                }
            }
            
        }
        
        if (key != null)
            kwargs.put(key, null);
        
        return this;
    }
    
    public CommandCall parse(String... args) {
        return parse(args, 0);
    }
    
    // GETTERS
    
    /**
     * Returns whether no arguments were provided to this command call.
     *
     * @return whether the call is empty
     */
    public boolean isEmpty() {
        return args.isEmpty() && kwargs.isEmpty();
    }
    
    public boolean isStrictOrder() {
        return strictOrder;
    }
    
    public boolean isStrictParams() {
        return strictParams;
    }
    
    /**
     * Returns the amount of ordered arguments in this call.
     *
     * @return the amount of ordered arguments
     */
    public int getArgCount() {
        return args.size();
    }
    
    /**
     * Returns the amount of keyword arguments in this call.
     *
     * @return the amount of keyword arguments
     */
    public int getKwArgCount() {
        return kwargs.size();
    }
    
    /**
     * Returns all indexed arguments, starting at a given index.
     *
     * @param start the start index
     * @return the arguments
     */
    @NotNull
    public String[] getArgs(int start) {
        return Arrays.copyOfRange(getArgs(), start, getArgCount());
    }
    
    /**
     * Returns a copy of all indexed arguments.
     *
     * @return all indexed arguments
     */
    @NotNull
    public String[] getArgs() {
        return args.toArray(new String[args.size()]);
    }
    
    /**
     * Returns all keyword arguments in an immutable map.
     *
     * @return all keyword arguments
     */
    @NotNull
    public Map<String, String> getKwArgs() {
        return Collections.unmodifiableMap(kwargs);
    }
    
    /**
     * <p>
     *     Returns an argument with a given index.
     * </p>
     * <blockquote>
     *     Example:
     *     <br><code>call: pig cow</code>
     *     <br><code>get(0) -> "pig"</code>
     *     <br><code>get(1) -> "cow"</code>
     * </blockquote>
     *
     * @param index the index
     * @return the argument
     * @throws IndexOutOfBoundsException if the index >= amount of arguments
     */
    @NotNull
    public String get(int index) {
        return args.get(index);
    }
    
    /**
     * <p>
     *     Returns an argument with a given index or a default value.
     * </p>
     * <blockquote>
     *     Example:
     *     <br><code>call: pig cow</code>
     *     <br><code>get(0) -> "pig"</code>
     *     <br><code>get(1) -> "cow"</code>
     * </blockquote>
     *
     * @param index the index
     * @param def the default value
     * @return the argument
     */
    public String getOrDefault(int index, String def) {
        return (index < getArgCount())? get(index) : def;
    }
    
    /**
     * Returns the value of a parameter with given keyword.
     *
     * @param keyword they keyword
     * @return the arguments for that keyword
     * @throws IllegalArgumentException if the call does not have the given keyword
     */
    @NotNull
    public String get(String keyword) {
        if (!kwargs.containsKey(keyword))
            throw new IllegalArgumentException(keyword);
        return kwargs.get(keyword);
    }
    
    /**
     * Returns the value of a parameter with a keyword matched by the given RegEx pattern.
     *
     * @param regex they regex pattern
     * @return the arguments for that keyword
     * @throws IllegalArgumentException if the call does not have the given keyword
     */
    @NotNull
    public String getMatch(@RegExp String regex) {
        Pattern pattern = Pattern.compile(regex);
        
        for (Map.Entry<String, String > entry : kwargs.entrySet()) {
            if (pattern.matcher(entry.getKey()).find())
                return entry.getValue();
        }
    
        throw new IllegalArgumentException(regex);
    }
    
    /**
     * Returns the value of a parameter with given keyword or a default fallback value.
     *
     * @param keyword they keyword
     * @param def the default value
     * @return the arguments for that keyword
     * @throws IllegalArgumentException if the call does not have the given keyword
     */
    public String getOrDefault(@NotNull String keyword, @Nullable String def) {
        if (!kwargs.containsKey(keyword))
            return def;
        return kwargs.get(keyword);
    }
    
    // PREDICATES
    
    /**
     * Returns whether the given keyword is accepted by this call.
     *
     * @param keyword the keyword
     * @return whether the keyword is accepted
     * @see #isStrictParams()
     */
    public boolean acceptsKeyword(String keyword) {
        for (String pattern : validKwArgs)
            if (keyword.matches(pattern))
                return true;
        return false;
    }
    
    /**
     * Returns whether the command call includes a given keyword.
     *
     * @param keyword the keyword
     * @return whether the command was called with this keyword
     */
    public boolean hasKeyword(String keyword) {
        return kwargs.containsKey(keyword);
    }
    
    /**
     * Returns whether the command call includes a keyword that matches the given RegEx pattern.
     *
     * @param keyword the keyword
     * @return whether the command was called with this keyword
     */
    public boolean matchKeyword(@RegExp String keyword) {
        Pattern pattern = Pattern.compile(keyword);
        
        for (String kw : kwargs.keySet())
            if (pattern.matcher(kw).matches())
                return true;
        
        return false;
    }
    
    // MUTATORS
    
    public CommandCall setStrictOrder(boolean strictOrder) {
        this.strictOrder = strictOrder;
        return this;
    }
    
    public CommandCall setStrictKwArgs(boolean strictParams) {
        this.strictParams = strictParams;
        return this;
    }
    
    public CommandCall addValidKwArgs(@NotNull String... params) {
        this.validKwArgs.addAll(Arrays.asList(params));
        return this;
    }
    
    public CommandCall addValidKwArgs(@NotNull Set<String> params) {
        this.validKwArgs.addAll(params);
        return this;
    }
    
    // MISC
    
    @Override
    public String toString() {
        return CommandCall.class.getSimpleName()+
            "{args="+Arrays.toString(getArgs())+
            ", kwargs="+toString(kwargs)+"}";
    }
    
    @NotNull
    private static <T> String toString(Map<T, T> map) {
        StringBuilder builder = new StringBuilder("{");
        
        Iterator<Map.Entry<T, T>> iter = map.entrySet().iterator();
        boolean hasNext = iter.hasNext();
        while (hasNext) {
            Map.Entry<T, T> entry = iter.next();
            builder.append(entry.getKey()).append(": ").append(entry.getValue());
            if (hasNext = iter.hasNext())
                builder.append(", ");
        }
        
        return builder.append("}").toString();
    }
    
}
