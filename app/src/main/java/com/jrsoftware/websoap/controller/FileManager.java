package com.jrsoftware.websoap.controller;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class FileManager {

    public final static String PARENT_DIRECTORY = "..";
    public final static String THIS_DIRECTORY = ".";
    private static final String LOG_TAG = "FILE-MANAGER";

    Context context;

	private String externalDir;
    private String internalDir;
    private String cacheDir;

    /* Filters results for text files and directories */
    private static FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            File file = new File(dir, filename);
            return filename.contains(".txt") || file.isDirectory();
        }
    };

    private static FilenameFilter textFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.contains(".txt");
        }
    };
	
	public FileManager(Context context) {
        this.context = context;
        this.internalDir = trimSlashes(context.getFilesDir().getAbsolutePath());
        this.cacheDir = trimSlashes(context.getCacheDir().getAbsolutePath());

        if(isExternalStorageWritable()){
            //TODO Limit by API level
            File external = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if(external == null)
                this.externalDir = trimSlashes(Environment.getExternalStorageDirectory().getPath());
            else
                this.externalDir = trimSlashes(external.getAbsolutePath());
        }
        else{
            this.externalDir = this.internalDir;
        }
	}

    public Context getContext() {
        return context;
    }

    //-------------------------------Directory Methods-------------------------------------------//


    /**
     * Checks if external storage is available for read and write
     * https://developer.android.com/training/basics/data-storage/files.html
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to read
     * https://developer.android.com/training/basics/data-storage/files.html
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public String getExternalDirectory(){
        return String.format("%s/", externalDir);
    }

    public String getInternalDirectory(){
        return String.format("%s/", internalDir);
    }

    public String getCacheDirectory(){
        return String.format("%s/", cacheDir);
    }

    public boolean isDirectory(String path){
        File file = new File(path);
        return file.isDirectory();
    }

    public boolean createDirectory(String dir){
        return new File(dir).mkdirs();
    }

    //-------------------------------File Methods------------------------------------------------//

    public boolean exists(String path){
        File file = new File(path);
        return file.exists();
    }
	
	public String getString(int resId){
		return context.getString(resId);
	}

    /**
     * Returns an input stream for the raw resource at the provided id
     * @param resId - Resource id of the resource to be loaded
     * @return - Input Stream for the raw resource
     * @throws IOException
     */
    public InputStream getRawResourceStream(int resId) throws IOException{
        if(context == null)
            return null;

        return context.getResources().openRawResource(resId);
    }

    private void writeFile(String data, String fileName, String dir) throws IOException {
        writeFile(new String[]{data}, fileName, dir);
    }

    /**
     * Writes the provided data to a file based on the designated filename and directory
     * @param data - Array of Strings to be written to the file
     * @param fileName - Relative path of the file being written
     * @param dir - Path of the directory where the file will be written
     * @throws IOException
     */
    private void writeFile(String[] data, String fileName, String dir) throws IOException {
        if(fileName == null) {
            Log.w(LOG_TAG, "No File Path Provided. File Not Written.");
            return;
        }

        /* Creates and Appends the directory to the provided filename */
        if(dir != null && dir.length() > 0) {
            createDirectory(dir);
            fileName = String.format("%s/%s", dir, fileName);
        }

        //This Commented block was part of my previous method of writing files.
        /* Opens the Output Stream
        FileOutputStream fileStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

        /* Joins String Array into one string to be written to the file
        String text = TextUtils.join(" ", data);

        /* Writes the String in bytes to the file stream
        fileStream.write(text.getBytes());

        /* Closes the output stream
        fileStream.close();
        */

        File file = new File(fileName);
        FileOutputStream fileStream = new FileOutputStream(file);
        OutputStreamWriter outputStream = new OutputStreamWriter(fileStream);

        for(int i = 0; i < data.length; i++){
            outputStream.write(data[i]);
            if(i < data.length - 1)
                outputStream.write("\n");
        }
        outputStream.close();
        fileStream.close();
    }

    public void writeInternalFile(String data, String fileName, String dir) throws IOException {
        writeInternalFile(new String[]{data}, fileName, dir);
    }

    /**
     *
     * @param data - String data to be written
     * @param filePath - The absolute path
     * @throws IOException
     */
    public void writeInternalFile(String[] data, String filePath, String dir) throws IOException {
        if(filePath == null)
            return;

        if(dir == null) {
            dir = getInternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Writing to Default Directory: %s", dir));
        }
        else
            dir = toInternalPath(dir);

        writeFile(data, filePath, dir);
    }

    private void writeSerializable(Serializable object, String fileName, String dir)
                                            throws IOException {
        String filePath = dir + fileName;
        Log.d(LOG_TAG, String.format("writeSerializable: %s", filePath));

        FileOutputStream fileStream = new FileOutputStream(filePath);
        ObjectOutputStream output = new ObjectOutputStream(fileStream);

        output.writeObject(object);
        output.close();
        fileStream.close();
    }

    public void writeInternalSerializable(Serializable object, String fileName, String dir)
                                            throws IOException {
        if(fileName == null)
            return;

        if(dir == null) {
            dir = getInternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Writing to Default Directory: %s", dir));
        }
        else
            dir = toInternalPath(dir);

        writeSerializable(object, fileName, dir);
    }

    public void writeExternalFile(String data, String fileName, String dir) throws IOException {
        writeExternalFile(new String[]{data}, fileName, dir);
    }

    /**
     *
     * @param data - String data to be written
     * @param filePath - The absolute path
     * @throws IOException
     */
    public void writeExternalFile(String[] data, String filePath, String dir) throws IOException {
        if(filePath == null)
            return;

        if(dir == null) {
            dir = getExternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Writing to Default Directory: %s", dir));
        }
        else
            dir = toExternalPath(dir);

        writeFile(data, filePath, dir);
    }

    public void writeExternalSerializable(Serializable object, String fileName, String dir)
                                            throws IOException {
        if(fileName == null)
            return;

        if(dir == null) {
            dir = getExternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Writing to Default Directory: %s", dir));
        }
        else
            dir = toExternalPath(dir);

        writeSerializable(object, fileName, dir);
    }

    /**
     *
     * @param filePath - Relative Filepath of the file being accessed
     * @param dir - Path of the Directory the file can be found in
     * @return - Array of Strings for containing each line of the accessed file.
     * @throws IOException
     */
    private String[] readFile(String filePath, String dir) throws IOException {
        //Log.v(LOG_TAG, "readFile");
        if(filePath == null)
            return null;

        //Log.v(LOG_TAG, String.format("Reading from Directory: %s", dir));
        //Log.v(LOG_TAG, String.format("Filepath: %s", filePath));

        /* Updates the Filepath with the directory */
        if(dir != null && dir.length() > 0)
            filePath = String.format("%s/%s", dir, filePath);

        String temp;
        ArrayList<String> list = new ArrayList<>();

        /* Initializing Input Stream & Reader */
        FileInputStream fileStream = new FileInputStream(new File(filePath));
        InputStreamReader inputStream = new InputStreamReader(fileStream);
        BufferedReader input = new BufferedReader(inputStream);

        /* Reads Each line into an Array List */
        while((temp = input.readLine()) != null)
            list.add(temp);

        /* Close the Input Streams */
        input.close();
        inputStream.close();

        if(list.size() < 1)
            return null;

        String[] arr = new String[list.size()];
        arr = list.toArray(arr);

        return arr;
    }

    /**
     * Read all lines from the file at the specified path.
     * @param filePath - Path of the file to be read.
     * @return String Array of lines read from the file if successful, otherwise null
     * @throws IOException
     */
	public String[] readInternalFile(String filePath, String dir) throws IOException {
        if(dir == null) {
            dir = getInternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Reading from Default Directory: %s", dir));
        }
        else
            dir = toInternalPath(dir);
        
        return readFile(filePath, dir);
	}

    private Object readSerializable(String fileName, String dir)
                                            throws IOException, ClassNotFoundException{
        String filePath = dir + fileName;
        Log.d(LOG_TAG, String.format("readSerializable: %s", filePath));

        FileInputStream file = new FileInputStream(filePath);
        ObjectInputStream input = new ObjectInputStream(file);

        Object obj = input.readObject();
        input.close();
        file.close();

        return obj;
    }

    public Object readInternalSerializable(String fileName, String dir)
                                            throws IOException, ClassNotFoundException {
        if(dir == null) {
            dir = getInternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Reading from Default Directory: %s", dir));
        }
        else
            dir = toInternalPath(dir);

        return readSerializable(fileName, dir);
    }

    /**
     * Read all lines from the file at the specified path.
     * @param filePath - Path of the file to be read.
     * @return String Array of lines read from the file if successful, otherwise null
     * @throws IOException
     */
    public String[] readExternalFile(String filePath, String dir) throws IOException {
        if(dir == null) {
            dir = getExternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Reading from Default Directory: %s", dir));
        }
        else
            dir = toExternalPath(dir);

        return readFile(filePath, dir);
    }

    public Object readExternalSerializable(String fileName, String dir)
            throws IOException, ClassNotFoundException {
        if(dir == null) {
            dir = getExternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Reading from Default Directory: %s", dir));
        }
        else
            dir = toExternalPath(dir);

        return readSerializable(fileName, dir);
    }

    /**
     * Loads the list of files from the provided external directory
     * @param directory - Absolute Path of the directory to be loaded
     * @return - String array of files/subdirectories found at the provided path
     */
    public String[] loadExternalFileList(String directory) throws SecurityException {
        if(directory == null)
            return null;
        return loadFileList(toExternalPath(directory));
    }

    public String[] loadExternalTextFiles(String directory) throws SecurityException {
        if(directory == null)
            return null;
        return loadTextFiles(toExternalPath(directory));
    }

    /**
     * Loads the list of files from the provided internal directory
     * @param directory - Absolute Path of the directory to be loaded
     * @return - String array of files/subdirectories found at the provided path
     */
    public String[] loadInternalFileList(String directory) throws SecurityException {
        if(directory == null)
            return null;
        return loadFileList(toInternalPath(directory));
    }

    public String[] loadInternalTextFiles(String directory) throws SecurityException {
        if(directory == null)
            return null;
        return loadTextFiles(toInternalPath(directory));
    }

    /**
     * Loads the list of files from the provided directory
     * @param directory - Absolute Path of the directory to be loaded
     * @return - String array of files/subdirectories found at the provided path
     */
    @SuppressWarnings("unused")
	private String[] loadFileList(String directory) throws SecurityException {
        return loadFilesWithFilter(directory, filenameFilter);
	}

    private String[] loadTextFiles(String directory) throws SecurityException {
        return loadFilesWithFilter(directory, textFileFilter);
    }

    private String[] loadFilesWithFilter(String directory, FilenameFilter filter){
        if(directory == null)
            return null;
        createDirectory(directory);

        String[] fileNames;
        File file = new File(directory);

        /* If the directory exists, load all files and subdirectories */
        if(file.exists())
            fileNames = file.list(filter);
        else
            fileNames = null;

        return fileNames;
    }

    /**
     * Deletes the file at the specified filepath.
     * @param path - Relative File Path of the file to be deleted in the Internal Directory
     * @return - Returns true if the deletion occurred successfully, false otherwise
     */
    public boolean deleteInternalFile(String dir, String path) throws IOException {
        if(path == null) {
            return false;
        }

        if(dir == null) {
            dir = getInternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Deleting from Default Directory: %s", dir));
        }
        else
            dir = toInternalPath(dir);

        return deleteFile(dir, path);
    }

    /**
     * Deletes the file at the specified filepath.
     * @param path - Relative File Path of the file to be deleted in the External Directory
     * @return - Returns true if the deletion occurred successfully, false otherwise
     */
    public boolean deleteExternalFile(String dir, String path) throws IOException {
        if(path == null) {
            return false;
        }

        if(dir == null) {
            dir = getExternalDirectory();
            Log.w(LOG_TAG, String.format("Directory not specified. Deleting from Default Directory: %s", dir));
        }
        else
            dir = toExternalPath(dir);

        return deleteFile(dir, path);
    }

    /**
     * Deletes the directory and all files and subdirectories at the specified filepath.
     * @param path - Relative File Path of the directory to be deleted in the Internal Directory
     * @return - Returns true if the deletion occurred successfully, false otherwise
     */
    public boolean deleteInternalDirectory(String path) throws IOException {
        if(path == null)
            return false;

        return deleteDirectory(toInternalPath(path));
    }

    /**
     * Deletes the directory and all files and subdirectories at the specified filepath.
     * @param path - Relative File Path of the directory to be deleted in the External Directory
     * @return - Returns true if the deletion occurred successfully, false otherwise
     */
    public boolean deleteExternalDirectory(String path) throws IOException {
        if(path == null)
            return false;

        return deleteDirectory(toExternalPath(path));
    }

    /**
     * Deletes the file at the specified filepath.
     * @param path - Absolute File Path of the file to be deleted
     * @return - Returns true if the deletion occurred successfully, false otherwise
     */
    private boolean deleteFile(String dir, String path) throws IOException {
        if (path == null || path.length() < 1) {
            return false;
        }

        /* Updates the Filepath with the directory */
        if(dir != null && dir.length() > 0)
            path = String.format("%s/%s", dir, path);

        File file = new File(path);
        return file.delete();
    }

    /**
     * Deletes the directory and all files and subdirectories at the specified filepath.
     * @param path - Absolute File Path of the directory to be deleted
     * @return - Returns true if the deletion occurred successfully, false otherwise
     */
    private boolean deleteDirectory(String path) throws IOException {
        if (path == null || path.length() < 1)
            return false;

        File dir = new File(path);
        if (dir.isDirectory()) {
            String[] files = dir.list();
            for(String file : files){
                if(isDirectory(file))
                    deleteDirectory(FileManager.concatPath(path, file));
                else
                    deleteFile(path, file);
            }
        }
        else{
            return false;
        }

        return dir.delete();
    }

    /**
     * Returns the path adjusted to reference the app directory in external memory
     */
    @SuppressWarnings("unused")
    public String toExternalPath(String path){
        return concatPath(externalDir, path);
    }

    /**
     * Returns the path adjusted to reference the app directory in internal memory
     */
    public String toInternalPath(String path){
        return concatPath(internalDir, path);
    }

    /**
     * Returns the path adjusted to reference the app directory in cache
     */
    @SuppressWarnings("unused")
    public String toCachePath(String path){
        return concatPath(cacheDir, path);
    }

    /**
     * Returns the absolute path of a provided address, or null if not available
     * @param path - Relative address
     * @return - Absolute path of the address provided, or null if not available
     */
    public static String toAbsolutePath(String path){
        if(path == null)
            return THIS_DIRECTORY;
        path = trimSlashes(path);

        File file = new File(path);
        if(file.exists())
            return file.getAbsolutePath();
        else
            return THIS_DIRECTORY;
    }

    public static String toRelativePath(String path){
        if(path == null)
            return THIS_DIRECTORY;
        path = trimSlashes(path);

        String[] temp;
        int length;
        if(path.contains("/")){
            temp = path.split("/");
            length = temp.length;

            return temp[length - 1];
        }
        else{
            return path;
        }
    }

    public static String concatPath(String parentDir, String path){
        if(path == null)
            path = THIS_DIRECTORY;
        if(parentDir == null)
            return path;

        parentDir = trimSlashes(parentDir);
        path = trimSlashes(path);
        return String.format("%s/%s", parentDir, path);
    }

    public static String trimSlashes(String str){
        if(str == null)
            return "";

        int length = str.length();
        StringBuilder sb = new StringBuilder(str);

        if(str.charAt(length - 1) == '/')
            sb.deleteCharAt(length - 1);

        if(str.charAt(0) == '/')
            sb.deleteCharAt(0);

        return sb.toString();
    }

    public static String getParentDirectory(String path){
        if(path == null)
            return THIS_DIRECTORY;

        File file = new File(path);
        if(!file.exists())
            return THIS_DIRECTORY;

        String parent = file.getParent();
        if(parent == null)
            return PARENT_DIRECTORY;
        else
            return parent;
    }

    public static String sanitizeFilePath(String filePath){
        if(filePath == null)
            return "";

        return filePath.replaceAll("\\s|\\W+", "_");
    }
}