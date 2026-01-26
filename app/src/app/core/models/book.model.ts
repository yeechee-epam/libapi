import { AuthorModel } from './author.model'; // adjust the path as needed

export interface BookModel {
    id: number;
    name: string;
    authorName: string;
    // author: AuthorModel;
}