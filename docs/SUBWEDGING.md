# Subwedging

Since the wedges used in the layout file are referenced by their corresponding class name, you may have guessed that it is possible to create your own wedge similarly to how one would create a custom view. You would be correct in that assumption. That said, because there are so many different use cases for this, I'm not going to provide a full "tutorial", but rather explain the basics of how wedges can be created.

## Creating the Class

The absolute minimum requirements for a wedge class are that they must extend `me.jfenn.attribouter.wedges.Wedge` and have a public constructor that accepts an `android.content.res.XmlResourceParser`. However, there are several additional suggestions that can affect... things... if they are not followed:

- Because wedges are added to a `RecyclerView`, they can have a subclass extending `me.jfenn.attribouter.wedges.Wedge$ViewHolder`, though this is not required. This class can be defined as a generic type of the `Wedge` class that your wedge class is extending, if that makes any sense (I don't know what I'm saying, just look at the example below and you'll see what I mean).
- Inside the constructor, you must call `super()` with the resource of the layout file that you want your wedge to use. 
- You can then use the instance of `XmlResourceParser` to collect any XML arguments you want. 
- After this, your constructor should end with a call to `addChildren(XmlResourceParser)`, which will parse and add any child wedges to a list that you can access (if your element has child wedges and there is not a call to this method in your constructor, it is very likely that you will encounter several issues as a result). 

Here is a basic example of what has been described so far:

```java
public class MyWedge extends Wedge<MyWedge.MyViewHolder> {
  
  public MyWedge(XmlResourceParser parser) {
    super(R.layout.my_wedge_layout);
    //get attributes
    addChildren(parser);
  }
  
  //other methods
  
  public static class MyViewHolder extends ViewHolder {
  
    public MyViewHolder(View v) {
      super(v);
    }
  
  }
  
}
```

## Implementing Methods

There are also several abstract methods in `Wedge` that must be defined. Rather than explain all of them, I will simply implement them all in the above example along with comments explaining what should happen where.

```java
public class MyWedge extends Wedge<MyWedge.MyViewHolder> {

  //define information in your Wedge class
  
  public MyWedge(XmlResourceParser parser) {
    super(R.layout.my_wedge_layout);
    //instantiate attributes
    addChildren(parser);
  }
  
  @Override
  public MyViewHolder getViewHolder(View v) { //"v" is the inflated layout file that you passed the resource of in your call to super()
    return new MyViewHolder(v); //yep, that's all you really need
  }
  
  @Override
  public void bind(Context context, MyViewHolder viewHolder) {
    //here you bind the data in your wedge class to the views in the MyViewHolder instance
  }
  
  public static class MyViewHolder extends ViewHolder {
  
    //define views in your layout
  
    public MyViewHolder(View v) {
      super(v);
      //instantiate views - "view = v.findViewById(...);"
    }
  
  }
  
}
```

## GitHub Requests

If you wish to make a request to the GitHub API from your wedge, simply call `addRequest(GitHubData)` with an instance of one of the [classes extending GitHubData](../attribouter/src/main/java/me/jfenn/attribouter/data/github/), or you can create your own. Thanks to [GSON](https://github.com/google/gson)'s magic, this is ridiculously simple, and if you've gotten this far you can probably figure it out yourself just by looking at the source code.

After calling `addRequest()` with an instance of `GitHubData`, Attribouter will obtain and parse the data itself (as well as caching, combining duplicate requests, and other complicated things), and will pass it back to the `onInit(GitHubData)` method - which you should probably extend - of your wedge class, where you can modify any of your wedge's data with the information you have obtained. Once `onInit` has been called, Attribouter will notify the RecyclerView of a change to your wedge, and you wedge's `bind()` method will be called shortly afterwards.

 As an example, here is a wedge based on the previous examples that initially displays my name in a TextView as "James Fern", then makes a request to GitHub, correcting it to "James Fenn":
 
 ```java
public class MyWedge extends Wedge<MyWedge.MyViewHolder> {

  private String myName;
  
  public MyWedge(XmlResourceParser parser) {
    super(R.layout.this_is_a_textview);
    
    myName = "James Fern";
    addRequest(new UserData("TheAndroidMaster"));
    
    addChildren(parser);
  }
  
  @Override
  public MyViewHolder getViewHolder(View v) {
    return new MyViewHolder(v);
  }
  
  @Override
  public void bind(Context context, MyViewHolder viewHolder) {
    viewHolder.textView.setText(myName);
  }
  
  @Override
  public void onInit(GitHubData data) {
    if (data instanceof UserData) {
      UserData user = (UserData) data;
      myName = user.name;
    }
  }
  
  public static class MyViewHolder extends ViewHolder {
  
    TextView textView;
  
    public MyViewHolder(View v) {
      super(v);
      textView = (TextView) v;
    }
  
  }
  
}
```
