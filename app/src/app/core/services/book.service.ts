import { Injectable } from '@angular/core';
import { mergeMap, Observable, of } from 'rxjs';
import { environment as env } from '../../../environments/environment';
import { ApiResponseModel, BookModel, RequestConfigModel } from '../models';
import { ExternalApiService } from './external-api.service';

@Injectable({
    providedIn: 'root'
})
export class BookService {
    constructor(public externalApiService: ExternalApiService) {}

    getBooks = (): Observable<ApiResponseModel> => {
        console.log('getbooks')
        const config: RequestConfigModel = {
            url: `${env.api.serverUrl}/books`,
            method: 'GET',
            headers: {
                'content-type': 'application/json',
            },

        };

        return this.externalApiService.callExternalApi(config).pipe(
            mergeMap((response) => {
                const { data, error } = response;
                console.log(response)
                return of({
                    data: data ? (data as BookModel[]) : null,
                    error,
                });
            })
        );
    };
}