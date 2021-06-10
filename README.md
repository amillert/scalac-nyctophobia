# scalac-nyctophobia
##### Are you afraid of the dark? - Scalac.io recruitment task
--
### General overview
When solving the task I was asked to tackle the following subtasks:
1. Read / write files / images;
2. Come up with the solution to obtain the luminance value of the processed image to further naïvely classify the images;
3. Evaluate the naïve classification *model* obtaining its performance;
4. Save back the processed images with the attached metadata as files' names;
5. Run the classification concurrently.

### Technology stack
In the project, I've utilized [`ZIO`](https://zio.dev/ "ZIO's Homepage") library. I tried to structure the project modularly to be able to take advantage of the `ZIO's` `ZLayer` capabilities; however, ultimately I've postponed the idea. Applying `ZLayers` should be a straightforward task.

For the compilation, code formatting, etc. I've used [`sbt`](https://www.scala-sbt.org/ "SBT's homepage"), [`metals`](https://scalameta.org/metals/ "Metal's homepage").

### Installation & running
In order to get the project locally, one should clone the repository as follows:
```bash
$ git clone https://amillert@bitbucket.org/amillert/scalac-nyctophobia.git
```

After this step, one should have access to both code and all `*.sbt` files required to run the project. Since the project has been built and developed using `sbt`, one should envoke `sbt` tool by running a self-explanatory command in the projects directory:
```bash
$ sbt
```

The user will be greeted in the `sbt` console; additionally, the user will be informed about the current project by an info message:
```bash
[info] Welcome to the scalac-nyctophobia project!
sbt:scalac-nyctophobia> 
```

Despite, this step should not be needed to be run, for the sake of completeness, in order to compile the code, one should type and execute the command:
```bash
sbt:scalac-nyctophobia> compile
```

If no bugs are present in the codebase (which should, obviously, be the case), one should be able to run the code by typing a command of the following format:
```bash
sbt:scalac-nyctophobia> run <input-directory> <output-directory> <luminance-threshold>
```

Since there exists a single unique `zio.App` object in the project, the user doesn't have to specify which main method should be run so the example above (and below) should suffice to run the code. An example of the execution which should work right away after cloning the project can be found below:
```bash
sbt:scalac-nyctophobia> run photos/bright photos/out 18
```

According to the provided assumptions, the `<input-directory>` is flat and non-empty, it contains unbroken files, whilst the `<output-directory>` is empty (but doesn't necessarily have to); more importantly it must exist in the filesystem. The `<luminance-threshold>` must be an integer number `x \in [0, 100]`.

### Approach to obtaining the perceived luminance score of the image
The method utilized is not entirely proprietary. I've been inspired by already existing research, tools. However, I have not used anyone else's code nor existing projects - the whole implementation of the processing is my own. The approach is based on a few key steps:
1. retrieve `R`, `G`, `B` channels per pixels in the image;
2. normalize values by dividing each channel's value by `255` (max channel's value);
3. linearize obtained values;
4. weighting each linearized channel by `0.2126`; `0.7152`; `0.722` for the `R`, `G`, `B` channels, respectively;
5. summing up the weighted linearized channel values per pixel;
6. obtain the perceived luminance value of a pixel according to formula: `x * 903.3 if x \leq 0.008856); x^{1/3} * 116 - 16 otherwise`;
7. calculating the average luminance value per image as a mean.

The linearization and weighting steps are responsible for turning a color-image into a gray-scale.

### Approach to obtaining the luminance threshold's value
The pragmatic method to choosing the `<luminance-threshold>` value was based on simple observations. After ensuring the *model* yields the perceived luminance score correctly (I've applied it on both provided directories with photos, namely: `bright` and `too_dark)`, I've noticed a maximum value of the `dark` images was `15`, whilst the minimum value obtained for the bright images was `32`. Hence, the value somewhere in the range `x \in [20, 30]` seems to be more or less a safe choice. Of course, the observation is biased by a too-small sample of the annotated data - pictures with the attached metadata (directory informing whether the picture is `light` or `dark`). Thankfully, the parameter can be adjusted to one's liking since it's an argument provided when running the script allowing one to perform a few experiments and see what works best for the task.

### Model evaluation
The symbolic evaluation of the *model* is based on the ground-truth annotation provided as metadata attached to the pictures. The information given is whether the picture is *bright* or *too_dark* as provided in the directory name. Simple metrics, such as `precision`, `recall`, `f1-scores` could have been implemented easily but such an evaluation of the *model* on such a small data sample of the data would be a slight overkill. Additionally, the metadata is attached to the directory name, not even files' and my script loads the data from a single flat directory (assumption given in the task's description). Because of these limitations; I simply provide a ratio of both the `dark` and `bright` images to their total sum.

### Results
The results obtained by evaluating the *model* have been juxtaposed in the table below. The set of *attributes* - the percentage of images classified as `bright` and `dark`, have been provided on the x-axis, and different experiments on the y-axis. Therefore, each row corresponds to a separate run. The first one denoted as `Brights` has been run solely on the light pictures, the second one - `Too_Darks` - on the dark ones, the third one - `Both_Merged` - on the combined set of images from the two previous ones (with the names replaced according to their original class - directory), and the last one - `Screenshots` - constitutes my own pictures from a laptop (`316` semi-randomly drawn screenshots). Value denoted as `-` can be understood as `not present in the dataset`, e.g. in the first experiment, there have been no dark images, the model has not classified any of the images as such, hence the respective value has not been displayed.

| Experiment \Ratio  	| Light % 	| Dark % 	|
|--------------------	|---------	|--------	|
| Brights             	| 1.0       | -      	|
| Too_Darks           	| -       	| 1.0      	|
| Both_Merged        	| .5     	| .5    	|
| Screenshots        	| .8481  	| .1519 	|

As a side note, the ratio values obtained in the last experiment seem to be more or less intuitive. Only a small portion of the images have been classified as `dark` ones. The dark screenshots are primarily taken in the terminal, IDE, or videos with the dark background; however, the great majority of the images are simply screenshots from various websites, conversations, etc. which indicates that generally speaking, they would also be classified as bright by human annotators.

### Problems encountered
I'm not the most experienced at using the `ZIO` library but I wanted to give it a try while tackling a real problem. I've decided to learn a bit more while working on this project. I've failed trying to compose the `ZLayers`; therefore, I've backed out and used a normal services' composition. I'm not sure about the parallelization of the computations. It doesn't seem to be the most efficient. Memory doesn't grow too significantly when running which is good but the overall run seems to be lengthy, to say the least. That's all I could have done using `ZIO` at the moment. I'm very motivated to learn a lot more but that's all I could have achieved in these few days without any prior knowledge of the tool.
