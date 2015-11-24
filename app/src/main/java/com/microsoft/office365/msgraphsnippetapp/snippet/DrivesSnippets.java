/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp.snippet;

import com.microsoft.office365.microsoftgraphvos.BaseVO;
import com.microsoft.office365.microsoftgraphvos.FolderVO;
import com.microsoft.office365.microsoftgraphvos.ItemVO;
import com.microsoft.office365.msgraphapiservices.MSGraphDrivesService;

import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import static com.microsoft.office365.msgraphsnippetapp.R.array.create_me_file;
import static com.microsoft.office365.msgraphsnippetapp.R.array.create_me_folder;
import static com.microsoft.office365.msgraphsnippetapp.R.array.delete_me_file;
import static com.microsoft.office365.msgraphsnippetapp.R.array.download_me_file;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_drive;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_me_files;
import static com.microsoft.office365.msgraphsnippetapp.R.array.get_organization_drives;
import static com.microsoft.office365.msgraphsnippetapp.R.array.rename_me_file;
import static com.microsoft.office365.msgraphsnippetapp.R.array.update_me_file;

abstract class DrivesSnippets<Result> extends AbstractSnippet<MSGraphDrivesService, Result> {

    public DrivesSnippets(Integer descriptionArray) {
        super(SnippetCategory.drivesSnippetCategory, descriptionArray);
    }

    static DrivesSnippets[] getDrivesSnippets() {
        return new DrivesSnippets[]{
                // Marker element
                new DrivesSnippets(null) {

                    @Override
                    public void request(MSGraphDrivesService msGraphDrivesService, Callback callback) {
                        //No implementation
                    }
                },
                //Snippets

                /* Get the user's drive
                 * HTTP GET https://graph.microsoft.com/{version}/me/drive
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/drive_get
                 */
                new DrivesSnippets<Response>(get_me_drive) {
                    @Override
                    public void request(MSGraphDrivesService msGraphDrivesService, Callback<Response> callback) {
                        msGraphDrivesService.getDrive(getVersion(), callback);
                    }
                },

                 /* Get all of the drives in your tenant
                 * HTTP GET https://graph.microsoft.com/{version}/myOrganization/drives
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/drive_get
                 */
                new DrivesSnippets<Response>(get_organization_drives) {
                    @Override
                    public void request(MSGraphDrivesService msGraphDrivesService, Callback<Response> callback) {
                        msGraphDrivesService.getOrganizationDrives(getVersion(), callback);
                    }
                },
                 /*
                 * Get a file
                 * HTTP GET https://graph.microsoft.com/{version}/me/drive/root/children
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_list_children
                 */
                new DrivesSnippets<Response>(get_me_files) {
                    @Override
                    public void request(final MSGraphDrivesService msGraphDrivesService, final Callback<Response> callback) {
                        //Get first group
                        msGraphDrivesService.getCurrentUserFiles(getVersion(), callback);
                    }
                },
                 /*
                 * Create a file
                 * HTTP PUT https://graph.microsoft.com/{version}/me/drive/root/children/{filename}/content
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_post_children
                 */
                new DrivesSnippets<BaseVO>(create_me_file) {
                    @Override
                    public void request(final MSGraphDrivesService msGraphDrivesService, final Callback<BaseVO> callback) {
                        //Create a new file under root
                        TypedString fileContents = new TypedString("file contents");
                        msGraphDrivesService.putNewFile(
                                getVersion(),
                                UUID.randomUUID().toString(),
                                fileContents,
                                callback);
                    }
                },
                 /*
                 * Download the content of a file
                 * HTTP GET https://graph.microsoft.com/{version}/me/drive/items/{filename}/content
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_downloadcontent
                 */
                new DrivesSnippets<Response>(download_me_file) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<Response> callback) {
                        TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "text/plain";
                            }
                        };

                        msGraphDrivesService.putNewFile(getVersion(),
                                UUID.randomUUID().toString(),
                                body,
                                new Callback<BaseVO>() {

                                    @Override
                                    public void success(BaseVO file, Response response) {
                                        //download the file we created
                                        msGraphDrivesService.downloadFile(
                                                getVersion(),
                                                file.id,
                                                callback);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //pass along error to original callback
                                        callback.failure(error);
                                    }
                                });
                    }
                },
                /*
                 * Update the content of a file
                 * HTTP PUT https://graph.microsoft.com/{version}/me/drive/items/{filename}/content
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_update
                 */
                new DrivesSnippets<BaseVO>(update_me_file) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<BaseVO> callback) {
                        final TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "text/plain";
                            }
                        };
                        msGraphDrivesService.putNewFile(getVersion(),
                                UUID.randomUUID().toString(),
                                body,
                                new Callback<BaseVO>() {

                                    @Override
                                    public void success(BaseVO directoryObject, Response response) {
                                        final TypedString updatedBody = new TypedString("Updated file contents") {
                                            @Override
                                            public String mimeType() {
                                                return "application/json";
                                            }
                                        };
                                        //download the file we created
                                        msGraphDrivesService.updateFile(
                                                getVersion(),
                                                directoryObject.id,
                                                updatedBody,
                                                callback);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //pass along error to original callback
                                        callback.failure(error);
                                    }
                                });
                    }
                },
                /*
                 * Delete the content of a file
                 * HTTP DELETE https://graph.microsoft.com/{version}/me/drive/items/{fileId}/
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_delete
                 */
                new DrivesSnippets<BaseVO>(delete_me_file) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<BaseVO> callback) {
                        final TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };
                        msGraphDrivesService.putNewFile(
                                getVersion(),
                                UUID.randomUUID().toString(),
                                body,
                                new Callback<BaseVO>() {

                                    @Override
                                    public void success(BaseVO file, Response response) {
                                        //download the file we created
                                        msGraphDrivesService.deleteFile(
                                                getVersion(),
                                                file.id,
                                                callback);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //pass along error to original callback
                                        callback.failure(error);
                                    }
                                });
                    }
                },
                /*
                 * Renames a file
                 * HTTP PATCH https://graph.microsoft.com/{version}/me/drive/items/{fileId}/
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_update
                 */
                new DrivesSnippets<BaseVO>(rename_me_file) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<BaseVO> callback) {
                        final TypedString body = new TypedString("file contents") {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };
                        msGraphDrivesService.putNewFile(
                                getVersion(),
                                UUID.randomUUID().toString(),
                                body,
                                new Callback<BaseVO>() {

                                    @Override
                                    public void success(BaseVO file, Response response) {
                                        // Build contents of post body and convert to StringContent object.
                                        // Using line breaks for readability.

                                        ItemVO delta = new ItemVO();
                                        delta.name = UUID.randomUUID().toString();

                                        //download the file we created
                                        msGraphDrivesService.renameFile(
                                                getVersion(),
                                                file.id,
                                                delta,
                                                callback);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        //pass along error to original callback
                                        callback.failure(error);
                                    }
                                });
                    }
                },
                /*
                 * Creates a folder
                 * HTTP POST https://graph.microsoft.com/me/drive/root/children
                 * @see https://graph.microsoft.io/docs/api-reference/v1.0/api/item_post_children
                 */
                new DrivesSnippets<Response>(create_me_folder) {

                    @Override
                    public void request(
                            final MSGraphDrivesService msGraphDrivesService,
                            final Callback<Response> callback) {
                        ItemVO folder = new ItemVO();
                        folder.name = UUID.randomUUID().toString();
                        folder.folder = new FolderVO();
                        folder.conflictBehavior = "rename";
                        msGraphDrivesService.createFolder(getVersion(), folder, callback);
                    }
                }
        };
    }

    public abstract void request(MSGraphDrivesService msGraphDrivesService, Callback<Result> callback);
}