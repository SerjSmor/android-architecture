# Kotlin conversion of todo-mvp

## Summary 

I've converted the "todo-mvp" of "android-architecture" project to Kotlin using the Android Studio tooling (ctrl + alt + shift + k). When deciding whether to adopt Kotlin and switch to another language it's usefull seing some mid level complexity examples along side modern architecture.

If you are unfamiliar with different styles of Android architecture (MVC, MVP, MVW) or their implications I highly recommend going over them anyway if you haven’t yet, https://github.com/googlesamples/android-architecture. 

### AddEditTaskActivity code changes outline

Java Code
```
public class AddEditTaskActivity extends AppCompatActivity{

      public static final int REQUEST_ADD_TASK = 1;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
             //….
      }

     @Override
     public boolean onSupportNavigateUp() {
               onBackPressed();
               return ture;
     }

     @VisibleForTesting
     public IdlingResouce getCountingIdlingResource() {
                return EspressoIdlingResource.getIdlingResource();
     }
}
```
Kotlin Code
```
class AddEditTaskActivity : AppCompatActivity() {

     override fun onCreate(savedInstanceStats: Bundle?) {
              //….
     }

     override fun onSupportNavigateUp() : Boolean {
            onBackPressed()
            return true
     }

     val countingIdlingResource: IdlingResource
          @VisibleForTestnig
          get() = EspressoIdlingResource.getIdlingResource();

     companion Object {
          val REQUEST_ADD_TASK = 1
     }

}
```

### Semicolons
Kotlin doesn’t mandate semicolons. That’s the first step towards Kotlin’s attempt to reduce Java verbosity.

### Function declaration
Keywords ‘override’ and ‘fun’, are straightforward. Putting the return type after the method name is not.

`override fun onSupportNavigateUp() : Boolean`

First comes the method name then the return type, separated by a colon ‘:’. Got it. More on function declarations can be found https://kotlinlang.org/docs/reference/functions.html.


### Nullability: 
Kotlin decided to help us devs with our `NullPointerExceptions`. How? With the call safety operator `?` of course. Kotlin mandates we declare which class members, variables and parameters can be null or not. Declaring `Bundle?` in previous snippet implies that saveInstanceState might be null, hence a good well educated programmer must first make the appropriate check.

Yes, the following with result in a compilation error:

```
override fun onCreate(savedInstanceStats: Bundle?) {
           savedInstanceState.get(SOME_KEY);
}
```
But doing a lot of `if (savedInstanceState != null)` is kind of ugly. So Kotlin’s `?` is there for us again:
```
override fun onCreate(savedInstanceStats: Bundle?) {
           savedInstanceState?.get(SOME_KEY);
}
```
In case `saveInstanceState` reference is `null`, the method `get()` will not be called and the line would produce a null. This operator can be chained and removes a whole bunch of if-else blocks.

### Val and var – Kotlin’s mutable and immutable variables

`val` are constant values while `var` are variables.  And note – you don’t need to declare a class type. It’s part of Kotlin’s magic. Kotlin infers the type of the variable by the type of the initializer.

`val REQUEST_ADD_TASK = 1`

Here `REQUEST_ADD_TASK` is set to the constant value ‘1’ so Kotlin infers that it’s an integer.


------------------

 I've written a blog series, you might find interesting, about the whole experience at http://blog.safedk.com/technology/hello-kotlin-convert-android-project-part-1/, including installation instructions and some conclusions. 
Disclosure: I'm an employe of SafeDK.
